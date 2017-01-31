package com.theironyard.repositories;

import com.theironyard.entities.ArtsyImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtsyImageRepository extends JpaRepository<ArtsyImage, Integer> {
    ArtsyImage findByUrl(String url);
}
