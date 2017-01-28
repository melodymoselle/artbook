package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "artworks")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artwork{
    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column()
    @JsonIgnore
    private LocalDateTime updatedAt;

    @Column(unique = true)
    @JsonProperty("id")
    private String artsyArtworkId;

    @Column
    private String title;

    @Column
    private String category;

    @Column
    private String medium;

    @Column
    private String date;

    @Transient
    @JsonProperty("dimensions")
    private Map rawDims;

    @Column
    private String size;

    @Column
    @JsonProperty("collecting_institution")
    private String collectingInstitution;

    @Transient
    @JsonProperty("_links")
    private Map links;

    @Column
    private String imgBaseUrl;

    @Column
    @JsonProperty("image_rights")
    private String imgRights;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    private List<Artist> artists = new ArrayList<>();

    @ManyToMany(mappedBy = "liked")
    private List<User> likedBy;

    @ManyToMany(mappedBy = "disliked")
    private List<User> dislikedBy;

    public Artwork() {
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

    public String getArtsyArtworkId() {
        return artsyArtworkId;
    }

    public void setArtsyArtworkId(String artsyArtworkId) {
        this.artsyArtworkId = artsyArtworkId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getCollectingInstitution() {
        return collectingInstitution;
    }

    public void setCollectingInstitution(String collectingInstitution) {
        this.collectingInstitution = collectingInstitution;
    }

    public String getImgBaseUrl() {
        return imgBaseUrl;
    }

    public void setImgBaseUrl(String imgBaseUrl) {
        this.imgBaseUrl = imgBaseUrl;
    }

    public String getImgRights() {
        return imgRights;
    }

    public void setImgRights(String imgRights) {
        this.imgRights = imgRights;
    }

    public List<Artist> getArtists() {
        return artists;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }

    public void addArtist(Artist artist){
        this.artists.add(artist);
    }

    public void deleteArtist(Artist artist){
        this.artists.remove(artist);
    }

    public List<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<User> likedBy) {
        this.likedBy = likedBy;
    }

    public List<User> getDislikedBy() {
        return dislikedBy;
    }

    public void setDislikedBy(List<User> dislikedBy) {
        this.dislikedBy = dislikedBy;
    }

    public Map getRawDims() {
        return rawDims;
    }

    public void setRawDims(Map rawDims) {
        this.rawDims = rawDims;
    }

    public Map getLinks() {
        return links;
    }

    public void setLinks(Map links) {
        this.links = links;
    }

    @Override
    public String toString() {
        return "Artwork{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", artsyArtworkId='" + artsyArtworkId + '\'' +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", medium='" + medium + '\'' +
                ", date='" + date + '\'' +
                ", rawDims=" + rawDims +
                ", collectingInstitution='" + collectingInstitution + '\'' +
                ", links=" + links +
                ", imgRights='" + imgRights + '\'' +
                '}';
    }
}
