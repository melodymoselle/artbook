package com.theironyard.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Artist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Service
public class WikipediaService {
    public static final String BASE_URL = "https://en.wikipedia.org/w/api.php";
    public static final String QUERY_PARAMS = "?action=query&format=json&prop=extracts&exintro=1&explaintext=1&titles=";

    @Autowired
    RestTemplate restTemplate;

    public String getWikiIntro(Artist artist){
        ObjectMapper mapper = new ObjectMapper();
        String json = restTemplate.getForObject(BASE_URL + QUERY_PARAMS + artist.getName(), String.class);
        JsonNode root = null;
        try {
            root = mapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String extract = "";
        if (root != null && root.findValue("pageid") != null) {
            extract = root.findValue("extract").textValue();
        }
        return extract;
    }
}
