package com.theironyard.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ArtsyService {
    public static final String BASE_URL = "https://api.artsy.net/api";

    @Autowired
    RestTemplate restTemplate;


}
