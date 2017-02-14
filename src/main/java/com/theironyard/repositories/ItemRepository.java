package com.theironyard.repositories;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllOrderByCreatedAtDesc();

    @Query("SELECT distinct i FROM Item i LEFT JOIN i.artist a WHERE a IN :following AND i.createdAt > :prevLogin")
    List<Item> findItemsFromFollowing(@Param("following") Set<Artist> following, @Param("prevLogin") LocalDateTime prevLogin);

}
