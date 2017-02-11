package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.services.GoogleCSEService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GoogleCSEServiceTest {

    @Autowired
    GoogleCSEService google;

    @Autowired
    ArtistRepository artistRepo;


    @Test
    public void testGetArticlesByArtist(){
        Artist artist = new Artist();
        artist.setName("SIMON DE VLIEGER");
        artistRepo.save(artist);

        google.getArticlesByArtist(artist);
    }
}
