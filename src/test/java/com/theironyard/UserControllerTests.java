package com.theironyard;

import com.theironyard.controllers.UserController;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utitilties.PasswordStorage;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTests {
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String ARTIST_NAME_1 = "Gustav Klimt";
    private static final String ARTIST_NAME_2 = "Cindy Sherman";
    private static final String ARTWORK_TITLE_1 = "Dur Kiss";

    User user;
    Artist artist1;
    Artist artist2;
    Artwork artwork;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() throws PasswordStorage.CannotPerformOperationException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
        user = new User(USERNAME, PasswordStorage.createHash(PASSWORD));
        userRepo.save(user);
        artist1 = new Artist(ARTIST_NAME_1);
        artist2 = new Artist(ARTIST_NAME_2);
        artistRepo.save(artist1);
        artistRepo.save(artist2);
        artwork = new Artwork(ARTWORK_TITLE_1);
        artworkRepo.save(artwork);
    }

    @After
    public void after(){
        userRepo.deleteAll();
        artistRepo.deleteAll();
        artworkRepo.deleteAll();
    }

    @Test
    public void testVerifyPassword() throws Exception {
        String hash = PasswordStorage.createHash(PASSWORD);
        boolean correct = PasswordStorage.verifyPassword(PASSWORD, hash);

        assertTrue(correct);
    }

    @Test
    public void testLoginValid() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .param("username", USERNAME)
                        .param("password", PASSWORD)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/"));
    }

    @Test
    public void testLoginInvalidUsername() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .param("username", USERNAME + "A")
                        .param("password", PASSWORD)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(flash().attribute("message", "Invalid Username/Password")
        ).andExpect(view().name("redirect:/login"));
    }

    @Test
    public void testLoginInvalidPassword() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .param("username", USERNAME)
                        .param("password", PASSWORD + "A")
        ).andExpect(status().is3xxRedirection()
        ).andExpect(flash().attribute("message", "Invalid Username/Password")
        ).andExpect(view().name("redirect:/login"));
    }

    @Test
    public void testRegisterValid() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .param("username", USERNAME + "A")
                        .param("password", PASSWORD)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/discover"));

        User user = userRepo.findByUsername(USERNAME + "A");

        assertNotNull("User object not saved correctly.", user);
        assertTrue("Password not saved correctly.", PasswordStorage.verifyPassword(PASSWORD, user.getPassword()));
    }

    @Test
    public void testRegisterUsernameExists() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.post("/register")
                        .param("username", USERNAME)
                        .param("password", PASSWORD)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(flash().attribute("message", "That username is taken.")
        ).andExpect(view().name("redirect:/login#register"));
    }

    @Test
    public void testGetDiscoverPageWithFollowing() throws Exception {
        artist1.getSimilarTo().add(artist2);
        artistRepo.save(artist1);
        artist2.getSimilarTo().add(artist1);
        artistRepo.save(artist2);
        user.getFollowing().add(artist1);
        userRepo.save(user);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/discover")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(model().attribute("artists", containsInAnyOrder(artist2))
        ).andExpect(model().attribute("artists", not(containsInAnyOrder(artist1)))
        ).andExpect(view().name("discover"));
    }

    @Test
    public void testGetDiscoverPageNoFollowing() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/discover")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(model().attribute("artists", containsInAnyOrder(artist2, artist1))
        ).andExpect(view().name("discover"));
    }

    @Test
    public void testFollowArtist() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/follow")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
                        .param("artistId", String.valueOf(artist1.getId()))
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/artist?artistId="+String.valueOf(artist1.getId())));

        List<Artist> following = artistRepo.findByFollowedBy(user);
        assertEquals("Size of user's following set is incorrect.", 1, following.size());
        assertEquals("Name of followed artist is incorrect.", ARTIST_NAME_1, following.get(0).getName());
    }

    @Test
    public void testUnfollowArtist() throws Exception {
        user.getFollowing().add(artist1);
        userRepo.save(user);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/unfollow")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
                        .param("artistId", String.valueOf(artist1.getId()))
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/artist?artistId="+String.valueOf(artist1.getId())));

        List<Artist> following = artistRepo.findByFollowedBy(user);
        assertEquals("Size of user's following set is incorrect.", 0, following.size());
    }

    @Test
    public void testLikeItem() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/like")
                        .header("referer", "redirect:/artwork?artworkId="+String.valueOf(artwork.getId()))
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
                        .param("itemId", String.valueOf(artwork.getId()))
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/artwork?artworkId="+String.valueOf(artwork.getId())));

        List<Artwork> likes = artworkRepo.findByLikedBy(user);
        assertEquals("Size of user's likes set is incorrect.", 1, likes.size());
        assertEquals("Title of liked artwork is incorrect.", ARTWORK_TITLE_1, likes.get(0).getTitle());
    }

    @Test
    public void testUnlikeItem() throws Exception {
        user.getLiked().add(artwork);
        userRepo.save(user);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/unlike")
                        .header("referer", "redirect:/artwork?artworkId="+String.valueOf(artwork.getId()))
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
                        .param("itemId", String.valueOf(artwork.getId()))
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/artwork?artworkId="+String.valueOf(artwork.getId())));

        List<Artwork> likes = artworkRepo.findByLikedBy(user);
        assertEquals("Size of user's likes set is incorrect.", 0, likes.size());
    }
}
