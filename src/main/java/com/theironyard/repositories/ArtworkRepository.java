package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
    Artwork findByArtsyArtworkId(String artsyArtworkId);
    List<Artwork> findByArtist(Artist artist);
    Page<Artwork> findByArtist(Pageable pageable, Artist artist);


    @Query("SELECT a  FROM Artwork a LEFT JOIN a.likedBy l WHERE a.artist = ?1 GROUP BY a.id ORDER BY count(l) DESC")
    Page<Artwork> findByArtistOrderByLikes(Pageable pageable, Artist artist);

    @Query("SELECT a  FROM Artwork a LEFT JOIN a.likedBy l GROUP BY a.id ORDER BY count(l) DESC")
    Page<Artwork> findAllOrderByLikes(Pageable pageable);

    @Query("SELECT a FROM Artwork a WHERE artist IN :following")
    Page<Artwork> findArtworksByFollowing(Pageable pageable, @Param("following") Set following);


}
