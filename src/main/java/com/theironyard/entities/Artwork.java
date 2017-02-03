package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

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

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    private String title;

    @Column
    private String category;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column
    private String medium;

    @Column
    private String date;

    @Transient
    @JsonProperty("dimensions")
    private Map<String, Map<String, Object>> rawDims;

    @Column
    private String size;

    @Column
    private String collectingInstitution;

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

    @Column
    private String imgZoom;

    @Column
    private String imageRights;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "artworks")
    @Fetch(value = FetchMode.SELECT)
    private List<Artist> artists = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "liked")
    @Fetch(value = FetchMode.SELECT)
    private List<User> likedBy;

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

    public String getImageRights() {
        return imageRights;
    }

    public void setImageRights(String imageRights) {
        this.imageRights = imageRights;
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

    public Map<String, Map<String, Object>> getRawDims() {
        return rawDims;
    }

    public void setRawDims(Map<String, Map<String, Object>> rawDims) {
        this.rawDims = rawDims;
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

    public String getImgZoom() {
        return imgZoom;
    }

    public void setImgZoom(String imgZoom) {
        this.imgZoom = imgZoom;
    }

    @Override
    public String toString() {
        return "Artwork{" +
                "id=" + id +
                ", artsyArtworkId='" + artsyArtworkId + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
