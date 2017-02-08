package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Video;
import com.theironyard.services.WikipediaService;
import com.theironyard.services.YoutubeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WikipediaServiceTests {

    @Autowired
    WikipediaService wiki;

    @Autowired
    YoutubeService youtube;

    Artist artist;

    @Before
    public void before(){

        artist = new Artist();
        artist.setName("Gustav klimt");
    }

    @Test
    public void testGetWikiIntro(){
        Artist artist = new Artist();
        artist.setName("Gustav klimt");

        wiki.getWikiIntro(artist);

    }

    @Test
    public void testGetYoutubeVideos(){
        List<Video> videos = youtube.getYoutubeVideos(artist);
        System.out.println(videos);
    }
}
