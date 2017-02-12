package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "artists")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artist{

    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;

    @Column
    private boolean loaded = false;

    @Column
    private boolean populated = false;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Column(unique = true)
    @JsonProperty("id")
    private String artsyArtistId;

    @Column(nullable = false)
    private String name;

    @Column
    private String birthday;

    @Column
    private String hometown;

    @Column
    private String location;

    @Column
    private String nationality;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    private String summary = "";

    @Column
    private String imgThumb = "";

    @Column
    private String imgLarge = "";

    @OneToMany(mappedBy="artist")
    private Set<Item> items = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Artist> similarTo = new HashSet<>();

    @ManyToMany(mappedBy = "similarTo", fetch = FetchType.EAGER)
    private Set<Artist> similarFrom = new HashSet<>();

    @ManyToMany(mappedBy = "following")
    private Set<User> followedBy = new HashSet<>();

    public Artist() {
    }

    //For Unit Testing only
    public Artist(String name) {
        this.name = name;
        this.populated = true;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", artsyArtistId='" + artsyArtistId + '\'' +
                ", name='" + name + '\'' +
                ", birthday='" + birthday + '\'' +
                ", hometown='" + hometown + '\'' +
                ", location='" + location + '\'' +
                ", nationality='" + nationality + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this){
            return true;
        }
        if (!(obj instanceof Artist)){
            return false;
        }
        Artist artist = (Artist) obj;

        //Only testing one attribute for Unit Tests
        return artist.getName().equals(this.name);
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

    public String getArtsyArtistId() {
        return artsyArtistId;
    }

    public void setArtsyArtistId(String artsyArtistId) {
        this.artsyArtistId = artsyArtistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHometown() {
        return hometown;
    }

    public void setHometown(String hometown) {
        this.hometown = hometown;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void addSimilarArtist(Artist artist){
        this.similarTo.add(artist);
    }

    public void deleteSimilarArtist(Artist artist){
        this.similarTo.remove(artist);
    }

    public Set<Artist> getSimilarTo() {
        return similarTo;
    }

    public void setSimilarTo(Set<Artist> similarTo) {
        this.similarTo = similarTo;
    }

    public Set<Artist> getSimilarFrom() {
        return similarFrom;
    }

    public void setSimilarFrom(Set<Artist> similarFrom) {
        this.similarFrom = similarFrom;
    }

    public Set<User> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(Set<User> followedBy) {
        this.followedBy = followedBy;
    }

    public boolean isPopulated() {
        return populated;
    }

    public void setPopulated(boolean populated) {
        this.populated = populated;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public void setLoaded(boolean loaded) {
        this.loaded = loaded;
    }

    public String getImgThumb() {
        return imgThumb;
    }

    public void setImgThumb(String imgThumb) {
        this.imgThumb = imgThumb;
    }

    public String getImgLarge() {
        return imgLarge;
    }

    public void setImgLarge(String imgLarge) {
        this.imgLarge = imgLarge;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Set<Item> getItems() {
        return items;
    }

    public void setItems(Set<Item> items) {
        this.items = items;
    }

    public void setItemsWithList(List<Artwork> artworks) {
        this.items = new HashSet<>(artworks);
    }
}
