package com.theironyard.repositories;

import com.theironyard.entities.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
}
