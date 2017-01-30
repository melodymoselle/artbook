package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    Artist findByArtsyArtistId(String artsyArtistId);
    List<Artist> findByLoadedAndPopulated(boolean loaded, boolean populated);
}
