package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.entities.ArtsyImage;
import com.theironyard.entities.Artwork;
import com.theironyard.models.Token;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtsyImageRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.services.ArtsyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtsyServiceTest {
    private static final String BASE_URL = "https://api.artsy.net/api";
    private static final String HEAD_AUTH = "X-Xapp-Token";

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    ArtsyService artsy;

    @Autowired
    ArtsyImageRepository artsyImgRepo;

    @Before
    public void before() {
    }

    @After
    public void after(){

    }

    @Test
    public void getAccessToken(){
        Token token = artsy.getAccessToken();

        assertNotNull("Error setting Token object", token);
        assertNotNull("Error setting token string", token.getToken());
        assertNotNull("Error setting token expires_at", token.getExpiresAt());
    }

    @Test
    public void getArtistById(){
        String artsyArtistId = "4d8b92b64eb68a1b2c000414";
        String name = "Gustav Klimt";
        String location = "Vienna, Austria";

        Artist artist = artsy.getSaveArtistById(artsyArtistId);

        assertNotNull("Error setting Artist object", artist);
        assertEquals("Error setting Artist Name", name, artist.getName());
        assertEquals("Error setting Artist Location", location, artist.getLocation());
    }

    @Test
    public void getSaveArtworkById(){
        String artsyArtworkId = "4d8b92eb4eb68a1b2c000968";
        String title = "Der Kuss (The Kiss)";
        String medium = "Oil and gold leaf on canvas";

        Artwork artwork = artsy.getSaveArtworkById(artsyArtworkId);

        assertNotNull("Error setting Artwork object", artwork);
        assertEquals("Error setting Artwork Title", title, artwork.getTitle());
        assertEquals("Error setting Artwork Location", medium, artwork.getMedium());
    }

    @Test
    public void getSaveArtworksByArtist(){
        Artist artist = new Artist();
        artist.setArtsyArtistId("4d8b92b64eb68a1b2c000414");
        artist.setName("Gustav Klimt");

        artsy.getSaveArtworksByArtist(artist);
        List<Artwork> artworks = artworkRepo.findByArtists(artist);

        assertNotNull("Error creating List of artworks",artworks);
        assertEquals("List size is incorrect", 6, artworks.size());
        assertEquals("First artwork was not set correctly", "4d8b92eb4eb68a1b2c000968", artworks.get(0).getArtsyArtworkId());
        assertEquals("Error setting Artist to Artwork", "4d8b92b64eb68a1b2c000414", artworks.get(0).getArtists().get(0).getArtsyArtistId());
    }

    @Test
    public void getSaveSimilarToByArtist(){
        Artist artist = new Artist();
        artist.setArtsyArtistId("4d8b92b64eb68a1b2c000414");
        artist.setName("Gustav Klimt");

        artist = artsy.getSaveSimilarToByArtist(artist);
        List<Artist> similarTo = artist.getSimilarTo();

        assertNotNull("Error setting list of similar artists", similarTo);
        assertEquals("Size of list of similar artist in incorrect", 18, similarTo.size());
        assertEquals("First artist was not set correctly", "4ee776e9d87cf50001000425", similarTo.get(0).getArtsyArtistId());
    }

    @Test
    public void parseImages(){
        List<String> imageVersions = new ArrayList<>();
        imageVersions.add("four_thirds");
        imageVersions.add("large");
        imageVersions.add("square");

        Map<String, Object> href = new HashMap<>();
        href.put("href", "https://d32dm0rphc51dk.cloudfront.net/3FBfL2Hs7UzA402R3UM2DQ/{image_version}.jpg");
        href.put("templated", true);

        Map<String, Map> imagesMap = new HashMap<>();
        imagesMap.put("image", href);

        List<ArtsyImage> images = artsy.parseImages(imageVersions, imagesMap);

        assertEquals(3, images.size());
        assertEquals("https://d32dm0rphc51dk.cloudfront.net/3FBfL2Hs7UzA402R3UM2DQ/four_thirds.jpg", images.get(0).getUrl());

        ArtsyImage image = artsyImgRepo.findOne(images.get(0).getId());
        assertNotNull(image);
    }

}
