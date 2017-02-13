package com.theironyard;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utitilties.PasswordStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoriesTests {
    private static final String USERNAME = "testUsername";
    private static final String PASSWORD = "testPassword";
    private static final String ARTIST_NAME_1 = "Gustav Klimt";
    private static final String ARTIST_NAME_2 = "Cindy Sherman";
    private static final String ARTIST_NAME_3 = "Rembrandt";
    private static final String ARTWORK_TITLE_1 = "Dur Kiss";
    private static final String ARTWORK_TITLE_2 = "Some Photo";

    User user;
    Artist artist1;
    Artist artist2;
    Artist artist3;
    Artwork artwork1;
    Artwork artwork2;


    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Before
    public void before() throws PasswordStorage.CannotPerformOperationException {
        user = new User(USERNAME, PasswordStorage.createHash(PASSWORD));
        userRepo.save(user);
        artist1 = new Artist(ARTIST_NAME_1);
        artist2 = new Artist(ARTIST_NAME_2);
        artist3 = new Artist(ARTIST_NAME_3);
        artistRepo.save(artist1);
        artistRepo.save(artist2);
        artistRepo.save(artist3);
        artist1.getSimilarTo().add(artist2);
//        artist2.getSimilarFrom().add(artist1);
//        artist1.getSimilarFrom().add(artist2);
        artist2.getSimilarTo().add(artist1);
        artist3.getSimilarTo().add(artist1);
        artwork1 = new Artwork(ARTWORK_TITLE_1);
        artwork2 = new Artwork(ARTWORK_TITLE_2);
        artworkRepo.save(artwork1);
        artworkRepo.save(artwork2);
        artwork1.setArtist(artist1);
        artist1.getItems().add(artwork1);
        artwork2.setArtist(artist2);
        artist2.getItems().add(artwork2);
        user.getFollowing().add(artist1);
        user.getFollowing().add(artist3);
//        artist1.getFollowedBy().add(user);
        user.getLiked().add(artwork1);
        artwork1.getLikedBy().add(user);
        artistRepo.save(artist1);
        artistRepo.save(artist2);
        artistRepo.save(artist3);
        artworkRepo.save(artwork1);
        artworkRepo.save(artwork2);
        userRepo.save(user);
    }

    @After
    public void after(){
        userRepo.deleteAll();
        artworkRepo.deleteAll();
        artistRepo.deleteAll();
    }

    @Test
    public void findSimilarFromFollowing(){
        user = userRepo.findByUsername(USERNAME);

        Page<Artist> suggestions = artistRepo.findSimilarFromFollowing(new PageRequest(0, 9), user.getFollowing());
        System.out.println(suggestions);

        List<Artist> artists = artistRepo.findSimilarFromFollowing(user.getFollowing());
        System.out.println(artists);
    }

    @Test
    public void findAllOrderByFollowers(){
        Page<Artist> artists = artistRepo.findAllOrderByFollowers(new PageRequest(0, 9));
        System.out.println(artists);
    }
}
