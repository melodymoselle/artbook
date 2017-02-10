package com.theironyard.repositories;

import com.theironyard.entities.Article;
import com.theironyard.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findByGoogleCacheId(String cacheId);
    List<Article> findByArtist(Artist artist);
    Article findByArtistAndUrl(Artist artist, String url);
}
