package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FartControllerTests {

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    WebApplicationContext wap;

    MockMvc mockMvc;

    @Before
    public void before() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
    }

    @After
    public void after(){

    }

    @Test
    public void addArtistToDB() throws Exception {
        String artsyArtistId = "4d8b92b64eb68a1b2c000414";

        mockMvc.perform(
                MockMvcRequestBuilders.post("/add-artist")
                        .param("artsyArtistId", artsyArtistId)
        ).andExpect(MockMvcResultMatchers.status().is3xxRedirection()
        ).andExpect(flash().attribute("artworksCount", 6)
        ).andExpect(view().name("redirect:/add-artist"));

        Artist artist = artistRepo.findByArtsyArtistId(artsyArtistId);
        Artwork artwork = artworkRepo.findByArtsyArtworkId("4d8b92eb4eb68a1b2c000968");

        assertNotNull("Error saving Artist to database", artist);
        assertEquals("Error setting Artist name", "Gustav Klimt", artist.getName());
//        assertEquals("Number of artworks is incorrect", 6, artist.getArtworks().size());
//        assertEquals("First Artwork is not correct", "4d8b92eb4eb68a1b2c000968", artist.getArtworks().get(0).getArtsyArtworkId());
        assertEquals("Artwork not saved correctly", "Gustav Klimt", artwork.getArtist().getName());
    }
}
