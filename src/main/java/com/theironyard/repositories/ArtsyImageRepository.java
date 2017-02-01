package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.ArtsyImage;
import com.theironyard.entities.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArtsyImageRepository extends JpaRepository<ArtsyImage, Integer> {
    ArtsyImage findByUrl(String url);
    List<ArtsyImage> findByArtist(Artist artist);
    List<ArtsyImage> findByArtwork(Artwork artwork);

    ArtsyImage findByVersionAndArtist(String version, Artist artist);
    ArtsyImage findByVersionAndArtwork(String version, Artwork artwork);
}
