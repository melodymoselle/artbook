package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
    Artwork findByArtsyArtworkId(String artsyArtworkId);
    List<Artwork> findByArtists(Artist artist);
    Page<Artwork> findByArtists( Pageable pageable, Artist artist);

    @Query("SELECT a  FROM Artwork a LEFT JOIN a.likedBy l GROUP BY a.id ORDER BY count(l) DESC")
    Page<Artwork> findAllOrderByLikes(Pageable pageable);

}
