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

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtbookController {

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
//        String artsyArtistId = "4d8b92b64eb68a1b2c000414";
//
//        mockMvc.perform(
//                MockMvcRequestBuilders.post("/add-artist")
//                        .param("artsyArtistId", artsyArtistId)
//        ).andExpect(MockMvcResultMatchers.status().is3xxRedirection()
//        ).andExpect(view().name("redirect:/add-artist"));
//
//        Artist artist = artistRepo.findByArtsyArtistId(artsyArtistId);
//
//        assertNotNull("Error saving Artist to database", artist);
//        assertEquals("Error setting Artist name", "Gustav Klimt", artist.getName());
    }
}
