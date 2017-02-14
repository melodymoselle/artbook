package com.theironyard.repositories;

import com.theironyard.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findByGoogleCacheId(String cacheId);
    List<Article> findByArtist(Artist artist);
    Article findByArtistAndUrl(Artist artist, String url);
    List<Article> findAllByOrderByCreatedAtDesc();
    List<Article> findAllByLikedBy(User user);


    @Query("SELECT distinct c FROM Article c LEFT JOIN c.artist a WHERE a IN :following AND c.createdAt > :prevLogin")
    List<Article> findLatestFromFollowing(@Param("following") Set<Artist> following, @Param("prevLogin") LocalDateTime prevLogin);


}
