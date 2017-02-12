package com.theironyard;


import com.theironyard.controllers.UserController;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utitilties.PasswordStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtbookControllerTests {
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String ARTIST_NAME_1 = "Gustav Klimt";
    private static final String ARTIST_NAME_2 = "Cindy Sherman";
    private static final String ARTWORK_TITLE_1 = "Dur Kiss";
    private static final String ARTWORK_TITLE_2 = "Some Photo";

    User user;
    Artist artist1;
    Artist artist2;
    Artwork artwork1;
    Artwork artwork2;

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
        artwork1 = new Artwork(ARTWORK_TITLE_1);
        artwork2 = new Artwork(ARTWORK_TITLE_2);
        artworkRepo.save(artwork1);
        artwork1.setArtist(artist1);
        artist1.getItems().add(artwork1);
        artwork2.setArtist(artist2);
        artist2.getItems().add(artwork2);
        user.getFollowing().add(artist1);
        artist1.getFollowedBy().add(user);
        artistRepo.save(artist1);
        artistRepo.save(artist2);
        artworkRepo.save(artwork1);
        artworkRepo.save(artwork2);
        userRepo.save(user);
    }

    @After
    public void after(){
        userRepo.deleteAll();
        artworkRepo.deleteAll();
        artistRepo.deleteAll();
    }

    @Test
    public void testGetArtworksNoUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/artworks")
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(view().name("artworks")
        ).andExpect(model().attribute("artworks", hasItem(artwork1))
        ).andExpect(model().attribute("artworks", hasItem(artwork2)));
    }

    @Test
    public void testGetArtworksWithUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/artworks")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(view().name("artworks")
        ).andExpect(model().attribute("artworks", hasItem(artwork1))
        ).andExpect(model().attribute("artworks", not(hasItem(artwork2))));
    }

    @Test
    public void testGetArtistsNoUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/artists")
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(view().name("artists")
        ).andExpect(model().attribute("artists", hasItem(artist1))
        ).andExpect(model().attribute("artists", hasItem(artist2)));
    }
}
