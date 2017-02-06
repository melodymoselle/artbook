package com.theironyard.repositories;

import com.theironyard.entities.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Integer> {
    Article findByGoogleCacheId(String cacheId);
}
