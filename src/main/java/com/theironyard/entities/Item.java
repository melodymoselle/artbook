package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance
@DiscriminatorColumn(name="item_type")
@DiscriminatorOptions(force = true)
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @JsonIgnore
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    private Artist artist;

    @ManyToMany(mappedBy = "liked")
    private Set<User> likedBy = new HashSet<>();

    @Column
    private String title = "";

    @Column
    private String snippet = "";

    @Column
    private String imgThumb = "";

    @Column String url = "";

    @Transient
    private boolean currentlyLiked = false;

    @Transient
    private boolean latest = false;

    public Item() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Set<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<User> likedBy) {
        this.likedBy = likedBy;
    }

    public boolean isCurrentlyLiked() {
        return currentlyLiked;
    }

    public void setCurrentlyLiked(boolean currentlyLiked) {
        this.currentlyLiked = currentlyLiked;
    }

    public Object isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }

    public String getTitle() {
        if (title.length() > 30){
            return title.substring(0, 30) + "...";
        }
        return title;
    }

    public void setTitle(String title) {
        if (title.length()>254) {
            title = title.substring(0, 254);
        }
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        if (snippet.length()>254) {
            snippet = snippet.substring(0, 254);
        }
        this.snippet = snippet;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
