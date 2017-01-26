package com.theironyard.entities;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "artworks")
public class Artwork {
    @Id
    @GeneratedValue
    private int id;

    @Column(unique = true)
    private String artsyArtworkId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String name;

    @Column
    private String category;

    @Column
    private String medium;

    @Column
    private String date;

    @Column
    private String dimensions;

    @Column
    private String collectingInstitution;

    @Column
    private String imgBaseUrl;

    @Column
    private String imgRights;

    @ManyToMany
    private List<Artist> artist;

}
