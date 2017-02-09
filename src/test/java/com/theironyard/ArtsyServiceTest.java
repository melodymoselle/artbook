package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.models.Token;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.services.ArtsyService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.ref.PhantomReference;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ArtsyServiceTest {
    private static final String ARTIST_ID = "4d8b92b64eb68a1b2c000414";
    private static final String NAME = "Gustav Klimt";
    private static final String LOCATION = "Vienna, Austria";

    private static final String ARTWORK_ID = "4d8b92eb4eb68a1b2c000968";
    private static final String TITLE = "Der Kuss (The Kiss)";
    private static final String MEDIUM = "Oil and gold leaf on canvas";

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    ArtsyService artsy;

    @Test
    public void getAccessToken(){
        Token token = artsy.getAccessToken();

        assertNotNull("Error setting Token object", token);
        assertNotNull("Error setting token string", token.getToken());
        assertNotNull("Error setting token expires_at", token.getExpiresAt());
    }

    @Test
    public void getArtistById(){
        Artist artist = artsy.getArtistByArtsyId(ARTIST_ID);

        assertNotNull("Error setting Artist object", artist);
        assertEquals("Error setting Artist Name", NAME, artist.getName());
        assertEquals("Error setting Artist Location", LOCATION, artist.getLocation());
    }

    @Test
    public void getArtworksByArtist(){
        Artist artist = new Artist();
        artist.setArtsyArtistId(ARTIST_ID);
        artist.setName(NAME);

        List<Artwork> artworks = artsy.getArtworksByArtist(artist);

        assertNotNull("Error creating List of artworks", artworks);
        assertEquals("List size is incorrect", 6, artworks.size());
        assertEquals("First artwork was not set correctly", ARTWORK_ID, artworks.get(0).getArtsyArtworkId());
        assertEquals("Error setting Artist to Artwork", ARTIST_ID, artworks.get(0).getArtist().getArtsyArtistId());
    }

    @Test
    public void getSimilarToByArtist(){
        Artist artist = new Artist();
        artist.setArtsyArtistId(ARTIST_ID);
        artist.setName(NAME);

        List<Artist> similarTo = artsy.getSimilarToByArtist(artist);

        assertNotNull("Error setting list of similar artists", similarTo);
        assertEquals("Size of list of similar artist in incorrect", 18, similarTo.size());
        assertEquals("First artist was not set correctly", "4ee776e9d87cf50001000425", similarTo.get(0).getArtsyArtistId());
    }

}
