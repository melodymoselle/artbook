package com.theironyard.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Token {

    private String token;

    @JsonProperty("expires_at")
    private ZonedDateTime expiresAt;

    public Token() {
    }

    public boolean isValid(){
        return expiresAt.isBefore(ZonedDateTime.now());
    }

    @Override
    public String toString() {
        return "Token{" +
                "token='" + token + '\'' +
                ", expiresAt=" + expiresAt +
                '}';
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZonedDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(ZonedDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setExpiresAt(String expiresAt){
        this.expiresAt = ZonedDateTime.parse(expiresAt);
    }
}
