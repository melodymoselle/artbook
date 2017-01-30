package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
    Artwork findByArtsyArtworkId(String artsyArtworkId);
    List<Artwork> findByArtists(Artist artist);
}
