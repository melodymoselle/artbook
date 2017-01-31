package com.theironyard.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ArtsyImage{
    @Id
    @GeneratedValue
    private int id;

    private String version;

    private String url;

    private Artist artist;

    private Artwork artwork;

    public ArtsyImage(String version, String url) {
        this.version = version;
        this.url = url;
    }

    public ArtsyImage(String version, String url, Artist artist) {
        this.version = version;
        this.url = url;
        this.artist = artist;
    }

    public ArtsyImage(String version, String url, Artwork artwork) {
        this.version = version;
        this.url = url;
        this.artwork = artwork;
    }

}
