package com.theironyard.services;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Thumbnail;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Video;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.services.youtube.YouTube;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Service
public class YoutubeService {
    @Value("${google.youtube.key}")
    private String key;

    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static YouTube youtube;

    public void getYoutubeVideos(Artist artist){
        try {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("youtube-cmdline-search-sample").build();

            String queryTerm = artist.getName();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            String apiKey = key;
            search.setKey(apiKey);
            search.setQ(queryTerm);

            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            if (searchResultList != null) {

                while (searchResultList.iterator().hasNext()) {

                    SearchResult singleVideo = searchResultList.iterator().next();
                    ResourceId rId = singleVideo.getId();

                    if (rId.getKind().equals("youtube#video")) {
                        Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                        Video video = new Video();

                        video.setVideoId(rId.getVideoId());
                        video.setChannelTitle(singleVideo.getSnippet().getChannelTitle());
                        video.setTitle(singleVideo.getSnippet().getTitle());
                        video.setDescription(singleVideo.getSnippet().getDescription());
                        video.setThumbnail(thumbnail.getUrl());
                    }
                }
            }
        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
