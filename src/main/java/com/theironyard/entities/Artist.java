package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


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

    @Transient
    @JsonProperty("_links")
    private Map<String, Map> imagesMap;

    @Transient
    @JsonProperty("image_versions")
    private List<String> imageVersions = new ArrayList<>();

    @Column
    private String imgThumb;

    @Column
    private String imgLarge;

    @OneToMany(mappedBy="artist")
    private List<Article> articles = new ArrayList<>();

    @OneToMany(mappedBy="artist")
    private List<Artwork> artworks = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Artist> similarTo = new ArrayList<>();

    @ManyToMany(mappedBy = "similarTo")
    private List<Artist> similarFrom = new ArrayList<>();

    @ManyToMany(mappedBy = "following")
    private List<User> followedBy;

    @ManyToMany(mappedBy = "notInterested")
    private List<User> notInterestedBy;

    public Artist() {
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

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
    }

    public void addArtwork(Artwork artwork){
        if (!this.artworks.contains(artwork)) {
            this.artworks.add(artwork);
        }
    }

    public void deleteArtwork(Artwork artwork){
        this.artworks.remove(artwork);
    }

    public void addSimilarArtist(Artist artist){
        this.similarTo.add(artist);
    }

    public void deleteSimilarArtist(Artist artist){
        this.similarTo.remove(artist);
    }

    public List<Artist> getSimilarTo() {
        return similarTo;
    }

    public void setSimilarTo(List<Artist> similarTo) {
        this.similarTo = similarTo;
    }

    public List<Artist> getSimilarFrom() {
        return similarFrom;
    }

    public void setSimilarFrom(List<Artist> similarFrom) {
        this.similarFrom = similarFrom;
    }

    public List<User> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(List<User> followedBy) {
        this.followedBy = followedBy;
    }

    public List<User> getNotInterestedBy() {
        return notInterestedBy;
    }

    public void setNotInterestedBy(List<User> notInterestedBy) {
        this.notInterestedBy = notInterestedBy;
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

    public Map<String, Map> getImagesMap() {
        return imagesMap;
    }

    public void setImagesMap(Map<String, Map> imagesMap) {
        this.imagesMap = imagesMap;
    }

    public List<String> getImageVersions() {
        return imageVersions;
    }

    public void setImageVersions(List<String> imageVersions) {
        this.imageVersions = imageVersions;
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

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    public void addArticle(Article article){
        if (!this.articles.contains(article)){
            this.articles.add(article);
        }
    }

    public void deleteArticle(Article article){
        this.articles.remove(article);
    }
}
