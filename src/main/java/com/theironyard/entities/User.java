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

    @Column
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
    private Set<Item> liked = new HashSet<>();

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

    public Set<Artist> getFollowing() {
        return following;
    }

    public void setFollowing(Set<Artist> following) {
        this.following = following;
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

    public boolean isLiked(Item item){
        return this.liked.contains(item);
    }
}
