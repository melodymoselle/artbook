package com.theironyard.services;

import com.theironyard.entities.Artist;
import com.theironyard.models.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
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
        if (!token.isValid()){
            token = getAccessToken();
        }

        String url = BASE_URL + "/artists/" + artist_id;
        HttpHeaders headers = new HttpHeaders();
        headers.set(HEAD_AUTH, token.getToken());
        HttpEntity request = new HttpEntity(headers);

        HttpEntity<Artist> response = restTemplate.exchange(url, HttpMethod.GET, request, Artist.class);

        return response.getBody();
    }

    public Token getAccessToken(){
        Map<String, String> request = new HashMap<>();
        request.put("client_id", id);
        request.put("client_secret", secret);

        return restTemplate.postForObject(BASE_URL+"/tokens/xapp_token", request, Token.class);
    }


}
