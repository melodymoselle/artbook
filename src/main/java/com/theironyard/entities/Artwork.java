package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artworks")
public class Artwork{
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(unique = true)
    private String artsyArtworkId;

    @Column
    private String name;

    @Column
    private String category;

    @Column
    private String medium;

    @Column
    private String date;

    @Column
    private String dimensions;

    @Column
    private String collectingInstitution;

    @Column
    private String imgBaseUrl;

    @Column
    private String imgRights;

    @ManyToMany
    private List<Artist> artist;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
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

    public List<Artist> getArtist() {
        return artist;
    }

    public void setArtist(List<Artist> artist) {
        this.artist = artist;
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
}
