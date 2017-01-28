package com.theironyard.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArtsyService {
    private static final String BASE_URL = "https://api.artsy.net/api";
    private static final String HEAD_AUTH = "X-Xapp-Token";

    @Value("${artsy.client_id}")
    private String id;

    @Value("${artsy.client_secret}")
    private String secret;

    private Token token;


    @Autowired
    RestTemplate restTemplate;


    @PostConstruct
    public void init(){
        token = getAccessToken();
    }

    public Artist getArtistById(String artist_id){
        String url = BASE_URL + "/artists/" + artist_id;
        HttpEntity request = getRequest();
        HttpEntity<Artist> response = restTemplate.exchange(url, HttpMethod.GET, request, Artist.class);

        return response.getBody();
    }

    public Artwork getArtworkById(String artwork_id){
        String url = BASE_URL + "/artworks/" + artwork_id;
        HttpEntity request = getRequest();
        HttpEntity<Artwork> response = restTemplate.exchange(url, HttpMethod.GET, request, Artwork.class);

        return response.getBody();
    }

    public List<Artwork> getArtworksByArtist(Artist artist){
        //Gets total artwork count
        String url = BASE_URL + "/artworks?total_count=1&size=1&artist_id=" + artist.getArtsyArtistId();
        HttpEntity request = getRequest();
        HttpEntity<HashMap> response = restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class);
        int count = ((int) response.getBody().get("total_count"));
        //Uses 'count' so that response is not paginated
        url = BASE_URL + "/artworks?size="+count+"&artist_id=" + artist.getArtsyArtistId();
        HashMap embedded = (HashMap)restTemplate.exchange(url, HttpMethod.GET, request, HashMap.class).getBody().get("_embedded");
        List<HashMap> rawArtworks = (List)embedded.get("artworks");
        List<Artwork> artworks = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        Artwork artwork;
        for (HashMap art : rawArtworks){
            artwork = mapper.convertValue(art, Artwork.class);
            artwork.addArtist(artist);
            artworks.add(artwork);
        }
        return artworks;
    }



    public HttpEntity getRequest(){
        if (!token.isValid()){
            token = getAccessToken();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEAD_AUTH, token.getToken());
        return new HttpEntity(headers);
    }

    public Token getAccessToken(){
        Map<String, String> request = new HashMap<>();
        request.put("client_id", id);
        request.put("client_secret", secret);

        return restTemplate.postForObject(BASE_URL+"/tokens/xapp_token", request, Token.class);
    }

}
