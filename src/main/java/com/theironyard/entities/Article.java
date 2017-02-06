package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue
    private int id;
    @Column
    @JsonProperty("cacheId")
    private String googleCacheId;
    @Column
    @JsonProperty("link")
    private String url;
    @Column
    private String title;
    @Column
    private String snippet;
    @Column
    private String imgThumb;
    private String ingLarge;
    @ManyToOne
    private Artist artist;

    public Article() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGoogleCacheId() {
        return googleCacheId;
    }

    public void setGoogleCacheId(String googleCacheId) {
        this.googleCacheId = googleCacheId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getIngLarge() {
        return ingLarge;
    }

    public void setIngLarge(String ingLarge) {
        this.ingLarge = ingLarge;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }
}
