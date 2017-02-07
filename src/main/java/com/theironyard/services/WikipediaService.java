package com.theironyard.services;

import com.theironyard.entities.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

public class WikipediaService {
    public static final String BASE_URL = "https://en.wikipedia.org/w/api.php";
    public static final String QUERY_PARAMS = "?action=query&format=json&prop=extracts&exintro=1&explaintext=1&titles=";

    @Autowired
    RestTemplate restTemplate;

    public void getWikiIntro(Artist artist){

    }
}
