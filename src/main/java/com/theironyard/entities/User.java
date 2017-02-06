package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User{
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column()
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private rights privileges = rights.SUBSCRIBER;

    public enum rights {
        SUBSCRIBER, ADMINISTRATOR
    }

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Artist> following = new ArrayList<>();

    @ManyToMany
    private List<Artist> notInterested;

    @ManyToMany
    private List<Artwork> liked;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public rights getPrivileges() {
        return privileges;
    }

    public void setPrivileges(rights privileges) {
        this.privileges = privileges;
    }

    public void addFollowing(Artist artist){
        if (!this.following.contains(artist)) {
            this.following.add(artist);
        }
    }

    public void deleteFollowing(Artist artist){
        this.following.remove(artist);
    }

    public List<Artist> getFollowing() {
        return following;
    }

    public void setFollowing(List<Artist> following) {
        this.following = following;
    }

    public List<Artist> getNotInterested() {
        return notInterested;
    }

    public void setNotInterested(List<Artist> notInterested) {
        this.notInterested = notInterested;
    }

    public void addLiked(Artwork artwork){
        if (!this.liked.contains(artwork)) {
            this.liked.add(artwork);
        }
    }

    public void deleteLiked(Artwork artwork){
        this.liked.remove(artwork);
    }

    public List<Artwork> getLiked() {
        return liked;
    }

    public void setLiked(List<Artwork> liked) {
        this.liked = liked;
    }

    public boolean isFollowing(Artist artist){
        return this.following.contains(artist);
    }

    public boolean isLiked(Artwork artwork){
        return this.liked.contains(artwork);
    }
}
