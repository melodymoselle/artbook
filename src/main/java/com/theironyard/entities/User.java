package com.theironyard.entities;

import com.theironyard.archive.Item;

import javax.persistence.*;
import java.time.LocalDateTime;
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

    private enum rights {
        SUBSCRIBER, ADMINISTRATOR
    }

    @ManyToMany
    private List<Artist> following;

    @ManyToMany
    private List<Artist> notInterested;

    @ManyToMany
    private List<Item> liked;

    @ManyToMany
    private List<Item> disliked;

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
        this.following.add(artist);
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

    public List<Item> getLiked() {
        return liked;
    }

    public void setLiked(List<Item> liked) {
        this.liked = liked;
    }

    public List<Item> getDisliked() {
        return disliked;
    }

    public void setDisliked(List<Item> disliked) {
        this.disliked = disliked;
    }
}
