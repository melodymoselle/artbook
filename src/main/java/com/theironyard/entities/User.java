package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Rights privileges = Rights.SUBSCRIBER;

    public enum Rights {
        SUBSCRIBER, ADMINISTRATOR
    }

    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Artist> following = new HashSet<>();

    @ManyToMany
    private Set<Artist> notInterested;

    @ManyToMany
    private Set<Item> liked;

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

    public Rights getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Rights privileges) {
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

    public Set getFollowing() {
        return following;
    }

    public void setFollowing(Set<Artist> following) {
        this.following = following;
    }

    public Set<Artist> getNotInterested() {
        return notInterested;
    }

    public void setNotInterested(Set<Artist> notInterested) {
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

    public Set<Item> getLiked() {
        return liked;
    }

    public void setLiked(Set<Item> liked) {
        this.liked = liked;
    }

    public boolean isFollowing(Artist artist){
        return this.following.contains(artist);
    }

    public boolean isLiked(Artwork artwork){
        return this.liked.contains(artwork);
    }
}
