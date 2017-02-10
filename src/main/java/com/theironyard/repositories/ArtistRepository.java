package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    Artist findByArtsyArtistId(String artsyArtistId);
    List<Artist> findByLoadedAndPopulated(boolean loaded, boolean populated);
    List<Artist> findByFollowedBy(User user);
    Page<Artist> findAll(Pageable pageable);
    Page<Artist> findByFollowedBy(Pageable pageable, User user);

    @Query("SELECT a  FROM Artist a LEFT JOIN a.followedBy f WHERE a.populated = true GROUP BY a.id ORDER BY count(f) DESC")
    Page<Artist> findAllOrderByFollowers(Pageable pageable);

    @Query(value = "SELECT * FROM artists a LEFT JOIN artists_similar_to simTo ON a.id = simTo.similar_to_id WHERE simTo.similar_from_id = :similarId AND a.populated = TRUE", nativeQuery = true)
    List<Artist> findSimilarAndPopulated(@Param("similarId") int similarId);

    @Query("SELECT DISTINCT a FROM Artist a LEFT JOIN a.similarTo simTo WHERE simTo IN :following AND a NOT IN :following AND a.populated = TRUE")
    Page<Artist> findSimilarFromFollowing(Pageable pageable, @Param("following") Set<Artist> following);

    Page<Artist> findByNameContainingIgnoreCaseAndPopulated(Pageable pageable, String searchTerm, boolean populated);

    @Query("SELECT a FROM Artist a WHERE a.name LIKE '%?1%' AND a.populated = TRUE")
    Page<Artist> searchForArtist(Pageable pageable, String searchTerm);
}
