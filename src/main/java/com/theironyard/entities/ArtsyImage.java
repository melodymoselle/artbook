package com.theironyard.entities;

import javax.persistence.*;

@Entity
@Table(name = "artsyImages")
public class ArtsyImage{
    @Id
    @GeneratedValue
    private int id;

    @Column
    private String version;

    @Column
    private String url;

    @ManyToOne
    private Artist artist;

    @ManyToOne
    private Artwork artwork;

    public ArtsyImage() {
    }

    public ArtsyImage(String version, String url) {
        this.version = version;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public void setArtwork(Artwork artwork) {
        this.artwork = artwork;
    }
}
