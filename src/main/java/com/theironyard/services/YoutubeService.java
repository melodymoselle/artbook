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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class YoutubeService {

    @Value("${google.youtube.key}")
    private String key;

    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;
    private static YouTube youtube;

    /**
     * Searches the Youtube API for 'video' resources that match 'Artist.getName()'.
     * Returns a List of Video objects created from each search result. Video objects are NOT saved.
     *
     * @param artist Artist object
     * @return List of Video objects
     */
    public List<Video> getYoutubeVideos(Artist artist){
        List<Video> videos = new ArrayList<>();
        try {
            youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            }).setApplicationName("f-Art").build();

            YouTube.Search.List search = youtube.search().list("id,snippet");

            search.setKey(key);
            search.setQ(artist.getName());
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the application uses.
            search.setFields("items(id/videoId,snippet/title,snippet/channelTitle,snippet/description,snippet/thumbnails/default/url)");
            search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();
            Iterator<SearchResult> iteratorSearchResults = searchResultList.iterator();

            if (searchResultList != null) {

                while (iteratorSearchResults.hasNext()) {

                    SearchResult singleVideo = iteratorSearchResults.next();
                    ResourceId rId = singleVideo.getId();

                    Thumbnail thumbnail = singleVideo.getSnippet().getThumbnails().getDefault();
                    Video video = new Video();

                    video.setVideoId(singleVideo.getId().getVideoId());
                    video.setChannelTitle(singleVideo.getSnippet().getChannelTitle());
                    video.setTitle(singleVideo.getSnippet().getTitle());
                    video.setDescription(singleVideo.getSnippet().getDescription());
                    video.setThumbnail(thumbnail.getUrl());
                    video.setArtist(artist);
                    videos.add(video);
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
        return videos;
    }
}
