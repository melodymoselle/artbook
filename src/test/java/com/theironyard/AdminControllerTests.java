package com.theironyard;

import com.theironyard.controllers.UserController;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.services.ArtsyService;
import com.theironyard.services.GoogleCSEService;
import com.theironyard.services.WikipediaService;
import com.theironyard.services.YoutubeService;
import com.theironyard.utitilties.PasswordStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AdminControllerTests {
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";

    private static final String ARTIST_ID = "4d8b92b64eb68a1b2c000414";
    private static final String NAME = "Gustav Klimt";
    private static final String LOCATION = "Vienna, Austria";

    private static final String ARTWORK_ID = "4d8b92eb4eb68a1b2c000968";
    private static final String TITLE = "Der Kuss (The Kiss)";
    private static final String MEDIUM = "Oil and gold leaf on canvas";

    User user;
    Artist artist;
    Artwork artwork;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Mock
    ArtsyService artsy;

    @Mock
    WikipediaService wiki;

    @Mock
    GoogleCSEService google;

    @Mock
    YoutubeService youtube;

    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() throws PasswordStorage.CannotPerformOperationException {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
        MockitoAnnotations.initMocks(this);
        user = new User(USERNAME, PasswordStorage.createHash(PASSWORD));
        user.setPrivileges(User.Rights.ADMINISTRATOR);
        userRepo.save(user);
        artist = new Artist(NAME);
        artist.setId(1);
    }

    @After
    public void after() {
        userRepo.deleteAll();
    }

    @Test
    public void getAddArtistPage() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/add-artist")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
        ).andExpect(status().is2xxSuccessful()
        ).andExpect(view().name("add-artist"));
    }

    @Test
    public void addArtistToDB() throws Exception {
        when(artsy.getArtistByArtsyId(ARTIST_ID)).thenReturn(artist);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/add-artist")
                        .sessionAttr(UserController.SESSION_USER, USERNAME)
                        .param("artsyArtistId", ARTIST_ID)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/artist?artistId=" + artist.getId()));

        Artist savedArtist = artistRepo.findOne(artist.getId());

        assertEquals("Artist not saved to DB correctly", artist.getName(), savedArtist.getName());
    }

    @Test
    public void addArtistToDBNoUser() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/add-artist")
                        .param("artsyArtistId", ARTIST_ID)
        ).andExpect(status().is3xxRedirection()
        ).andExpect(view().name("redirect:/error"));
    }

    @Test
    public void getArtistData(){

    }
}
