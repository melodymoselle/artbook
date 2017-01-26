package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String privilege;

    private enum privileges {
        SUBSCRIBER, ADMINISTRATOR
    }

    @ManyToMany
    private List<Artwork> following;

    @ManyToMany
    private List<Artwork> notInterested;

    @ManyToMany
    private List<Item> liked;

    @ManyToMany
    private List<Item> disliked;

}
