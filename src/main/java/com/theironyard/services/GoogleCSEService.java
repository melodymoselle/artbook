package com.theironyard.services;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;
import com.theironyard.entities.Article;
import com.theironyard.entities.Artist;
import com.theironyard.repositories.ArticleRepository;
import com.theironyard.repositories.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Service
public class GoogleCSEService {
    private static final long NUMBER_OF_RESULTS_RETURNED = 10;

    @Value("${google.cse_id}")
    private String cx;

    @Value("${google.api_key}")
    private String key;

    @Autowired
    ArticleRepository articleRepo;

    @Autowired
    ArtistRepository artistRepo;

    Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);

    /**
     * Gets articles from Google search API related to 'artist.name.'
     * Converts results to Article objects and returns a list. Unsaved.
     *
     * @param artist object with 'name' used for query
     * @return List of article objects related to 'artist'
     */
    public List<Article> getArticlesByArtist(Artist artist) {
        List<Article> articles = new ArrayList<>();
        try {
            Customsearch.Cse.List search = customsearch.cse().list(artist.getName().replace(" ", "+"));
            search.setCx(cx);
            search.setKey(key);
            Search results = search.execute();
            List<Result> items = results.getItems();

            if(items != null) {
                for (Result rs : items) {
                    Article article = articleRepo.findByGoogleCacheId(rs.getCacheId());
                    if (article == null) {
                        article = new Article();
                        article.setGoogleCacheId(rs.getCacheId());
                        article.setUrl(rs.getLink());
                        article.setTitle(rs.getTitle());
                        article.setSnippet(rs.getSnippet());
                        if (rs.getPagemap().size() > 1) {
                            article.setImgThumb(rs.getPagemap().get("cse_thumbnail").get(0).get("src").toString());
                            article.setImgLarge(rs.getPagemap().get("cse_image").get(0).get("src").toString());
                        }
                        article.setArtist(artist);
                        articles.add(article);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }
}
