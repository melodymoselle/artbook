package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.repositories.ArtistRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FArtApplicationTests {

	@Autowired
	ArtistRepository artistRepo;

//	@Test
//	public void testQuery() {
//        List<Artist> artists = artistRepo.findAllOrderByFollowedBy();
//        System.out.println(artists.size());
//        artists.forEach(artist -> System.out.println(artist.getName()));
//        System.out.println("done.");
//    }

}
