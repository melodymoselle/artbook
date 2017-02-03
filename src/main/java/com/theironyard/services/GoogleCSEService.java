package com.theironyard.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleCSEService {

    @Value("${google.cse_id}")
    private String id;

    @Value("${google.api_key}")
    private String key;
}
