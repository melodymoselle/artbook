package com.theironyard.archive;

import com.theironyard.entities.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
public class Item{
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String type;

    private enum type{
        ARTWORK, SHOW
    }

    @ManyToMany(mappedBy = "liked")
    private List<User> likedBy;

    @ManyToMany(mappedBy = "disliked")
    private List<User> dislikedBy;

    public Item() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
