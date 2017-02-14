package com.theironyard.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.DiscriminatorOptions;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance
@DiscriminatorColumn(name="item_type")
@DiscriminatorOptions(force = true)
@Table(name="items")
public class Item {
    @Id
    @GeneratedValue
    @JsonIgnore
    private int id;

    @Column(nullable = false)
    @JsonIgnore
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    @JsonIgnore
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    private Artist artist;

    @ManyToMany(mappedBy = "liked")
    private Set<User> likedBy = new HashSet<>();

    @Transient
    private boolean liked = false;

    @Transient
    private boolean latest = false;

    public Item() {
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

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public Set<User> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(Set<User> likedBy) {
        this.likedBy = likedBy;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isLatest() {
        return latest;
    }

    public void setLatest(boolean latest) {
        this.latest = latest;
    }
}
