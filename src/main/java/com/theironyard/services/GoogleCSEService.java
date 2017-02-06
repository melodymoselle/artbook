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
import java.util.List;
@Service
public class GoogleCSEService {

    @Value("${google.cse_id}")
    private String cx;

    @Value("${google.api_key}")
    private String key;

    @Autowired
    ArticleRepository articleRepo;

    @Autowired
    ArtistRepository artistRepo;

    Customsearch customsearch = new Customsearch(new NetHttpTransport(), new JacksonFactory(), null);

    public Artist getArticlesByArtist(Artist artist) {
        try {
            Customsearch.Cse.List search = customsearch.cse().list(artist.getName().replace(" ", "+"));
            search.setCx(cx);
            search.setKey(key);
            Search results = search.execute();
            List<Result> items = results.getItems();

            for (Result rs : items){
                Article article = articleRepo.findByGoogleCacheId(rs.getCacheId());
                if (article == null) {
                    article.setGoogleCacheId(rs.getCacheId());
                    article.setUrl(rs.getLink());
                    article.setTitle(rs.getTitle());
                    article.setSnippet(rs.getSnippet());
                    article.setImgThumb(rs.getImage().getThumbnailLink());
                    article.setIngLarge(rs.getImage().getContextLink());
                    article.setArtist(artist);
                    articleRepo.save(article);
                    artist.addArticle(article);
                }
            }
            artistRepo.save(artist);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return artist;
    }
}
