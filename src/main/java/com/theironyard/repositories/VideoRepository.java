package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoRepository extends JpaRepository<Video, Integer> {
    List<Video> findByArtist(Artist artist);
    Video findByVideoId(String videoId);
}
