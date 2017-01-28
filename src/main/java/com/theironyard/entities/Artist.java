package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.theironyard.services.ArtsyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artists")
public class Artist{
    @Autowired
    @Transient
    ArtsyService artsy;

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
    private String artsyArtistId;

    @Column(nullable = false)
    private String name;

    @Column
    @JsonProperty("sortable_name")
    private String sortableName;

    @Column
    private String gender;

    @Column
    private String birthday;

    @Column
    private String hometown;

    @Column
    private String location;

    @Column
    private String nationality;

    @Column
    @JsonIgnore
    private String imgBaseUrl;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "artists", cascade=CascadeType.ALL)
    @JsonIgnore
    private List<Artwork> artworks;

//    @ManyToMany(mappedBy = "similarArtists")
//    private List<Artist> similarArtists;

    @ManyToMany(mappedBy = "following")
    @JsonIgnore
    private List<User> followedBy;

    @ManyToMany(mappedBy = "notInterested")
    @JsonIgnore
    private List<User> notInterestedBy;


    public Artist() {
    }

    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", artsyArtistId='" + artsyArtistId + '\'' +
                ", name='" + name + '\'' +
                ", sortableName='" + sortableName + '\'' +
                ", gender='" + gender + '\'' +
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

    public String getSortableName() {
        return sortableName;
    }

    public void setSortableName(String sortableName) {
        this.sortableName = sortableName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getImgBaseUrl() {
        return imgBaseUrl;
    }

    public void setImgBaseUrl(String imgBaseUrl) {
        this.imgBaseUrl = imgBaseUrl;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }

    public void setArtworks(List<Artwork> artworks) {
        this.artworks = artworks;
    }

    public void addArtwork(Artwork artwork){
        this.artworks.add(artwork);
    }

    public void addArtwork(String artsyArtworkId){
        Artwork artwork = artsy.getArtworkById(artsyArtworkId);
    }

    public void deleteArtwork(Artwork artwork){
        this.artworks.remove(artwork);
    }

//    public List<Artist> getSimilarArtists() {
//        return similarArtists;
//    }
//
//    public void setSimilarArtists(List<Artist> similarArtists) {
//        this.similarArtists = similarArtists;
//    }

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
}
