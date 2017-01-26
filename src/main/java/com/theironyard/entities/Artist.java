package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artists")
public class Artist {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String artsyArtistId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private String name;

    @Column
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
    private String imgBaseUrl;

    @ManyToMany(mappedBy = "artist")
    private List<Artwork> artworks;



}
