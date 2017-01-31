package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    Artist findByArtsyArtistId(String artsyArtistId);
    List<Artist> findByLoadedAndPopulated(boolean loaded, boolean populated);
    List<Artist> findByFollowedBy(User user);

    @Query("SELECT a  FROM Artist a LEFT JOIN a.followedBy f WHERE a.populated = true GROUP BY a.id ORDER BY count(f) DESC")
    List<Artist> findAllPopulatedOrderByFollowedBy();


}
