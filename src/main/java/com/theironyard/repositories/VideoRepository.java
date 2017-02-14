package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    List<Video> findByArtist(Artist artist);
    List<Video> findAllOrderByCreatedAtDesc();

    @Query("SELECT distinct v FROM Video v LEFT JOIN v.artist a WHERE a IN :following AND v.createdAt > :prevLogin")
    List<Video> findLatestFromFollowing(@Param("following") Set<Artist> following, @Param("prevLogin") LocalDateTime prevLogin);


}
