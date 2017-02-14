package com.theironyard.controllers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.theironyard.entities.*;
import com.theironyard.repositories.*;
import com.theironyard.services.ArtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Set;

@Controller
public class ArtbookController {

    @Autowired
    ArtsyService artsy;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @Autowired
    ArticleRepository articleRepo;

    @Autowired
    VideoRepository videoRepo;

    @Autowired
    ItemRepository itemRepo;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String forward(){
        return "redirect:/artworks";
    }

    /**
     * If no user is logged in, gets all artworks ordered by number of likes.
     * If there is a user logged in, gets all artworks from the user's list of following artists.
     * Adds list of artworks to the model.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param page Current page number for database paging
     * @return model and 'artworks' view
     */
    @RequestMapping(path = "/artworks", method = RequestMethod.GET)
    public String getArtworks(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page){
        Page<Artwork> artworks = artworkRepo.findAllOrderByLikes(new PageRequest(page, 9));
        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            Set artists = user.getFollowing();
            if (artists.size() > 0) {
                artworks = artworkRepo.findArtworksByFollowing(new PageRequest(page, 9), artists);
            }
        }
        if(artworks.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artworks.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("artworks", artworks);
        model.addAttribute("pagingStub", "artworks");
        model.addAttribute("pageName", "Artworks");
        return "artworks";
    }

    /**
     * If no user, gets all artists ordered by number of followers. If there is a user,
     * gets all the artists from the user's following. Adds list of artists to the model.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param page Current page number for database paging
     * @return model and 'artists' view
     */
    @RequestMapping(path = "/artists", method = RequestMethod.GET)
    public String getArtists(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page){
        Page<Artist> artists = artistRepo.findAllOrderByFollowers(new PageRequest(page, 9));
        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            artists = artistRepo.findByFollowedBy(new PageRequest(page, 9), user);
        }
        if(artists.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artists.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("artists", artists);
        model.addAttribute("pagingStub", "artists");
        model.addAttribute("pageName", "Artists");
        return "artists";
    }

    /**
     * Gets Artist with 'artistId' as 'id', and related Articles, similar Artists,
     * Artworks, and videos. Adds to model. If user, and user is following, adds 'following'
     * to model, so view will display following button. If user is admin, adds 'admin' to
     * model, so view will display admin controls.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param artistId Id of artist to pull from DB
     * @param page Current page number for database paging
     * @return model and 'artists' view
     */
    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(HttpSession session, Model model, int artistId, @RequestParam(defaultValue = "0") int page){
        Artist artist = artistRepo.findOne(artistId);
        List<Article> articles = articleRepo.findByArtist(artist);
        List<Artist> similar = artistRepo.findSimilarAndPopulated(artist.getId());
        List<Artwork> artworks = artworkRepo.findByArtist(artist);
        List<Video> videos = videoRepo.findByArtist(artist);

        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            if (user.isFollowing(artist)){
                model.addAttribute("following", true);
            }
            for (Article a : articles){
                if (user.isLiked(a)){
                    a.setCurrentlyLiked(true);
                }
            }
            for (Video v : videos){
                if (user.isLiked(v)){
                    v.setCurrentlyLiked(true);
                }
            }

        }
        model.addAttribute("articles", articles);
        model.addAttribute("similar", similar);
        model.addAttribute("artworks", artworks);
        model.addAttribute("videos", videos);
        model.addAttribute("artist", artist);

        model.addAttribute("pageName", artist.getName());
        return "artist";
    }

    /**
     * Gets Artwork with 'artworkId' as 'id'. Adds to model. If user, displays 'like' button.
     * Is user 'likes' artwork, displays 'unlike' button. If user is admin, displays admin controls.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param artworkId Id of artwork to pull from DB
     * @return model and 'artists' view
     */
    @RequestMapping(path = "/artwork", method = RequestMethod.GET)
    public String getArtworkPage(HttpSession session, Model model, int artworkId){
        Artwork artwork = artworkRepo.findOne(artworkId);
        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            if (user.isLiked(artwork)){
                model.addAttribute("liked", true);
            }
        }
        model.addAttribute("artwork", artwork);
        model.addAttribute("pageName", artwork.getTitle());
        return "artwork";
    }

    @RequestMapping(path = "/error" , method = RequestMethod.GET)
    public String getErrorPage(Model model, String message){
        model.addAttribute("message", message);
        return "error";
    }

    /**
     * Gets search term from html form input and gets all artists with 'q'
     * and adds to model.
     *
     * @param model Model to be passed to the view
     * @param q search term from form input
     * @param page Current page number for database paging
     * @return model and 'search' view
     */
    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String getSearchResults(Model model, String q, @RequestParam(defaultValue = "0") int page){
        Page<Artist> artists = artistRepo.findByNameContainingIgnoreCaseAndPopulated(new PageRequest(page, 9), q, true);
        for (Artist artist : artists){
            List<Artwork> artworks = artworkRepo.findByArtistOrderByLikes(new PageRequest(page, 3), artist).getContent();
            artist.setItemsWithList(artworks);
        }

        model.addAttribute("artists", artists);
        if(artists.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artists.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("pagingStub", "search");
        model.addAttribute("pageName", "Search Results");
        return "search";
    }

    @RequestMapping(path = "/latest", method = RequestMethod.GET)
    public String getLatestPage(HttpSession session, Model model, RedirectAttributes redAtt){

        List<Item> items = itemRepo.findAllOrderByCreatedAtDesc();
        List<Artwork> artworks = artworkRepo.findAllOrderByCreatedAtDesc();
        List<Article> articles = articleRepo.findAllOrderByCreatedAtDesc();
        List<Video> videos = videoRepo.findAllOrderByCreatedAtDesc();

        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            items = itemRepo.findItemsFromFollowing(user.getFollowing(), user.getPrevLogin());
            artworks = artworkRepo.findLatestFromFollowing(user.getFollowing(), user.getPrevLogin());
            articles = articleRepo.findLatestFromFollowing(user.getFollowing(), user.getPrevLogin());
            videos = videoRepo.findLatestFromFollowing(user.getFollowing(), user.getPrevLogin());
        }

        model.addAttribute("items", items);
        model.addAttribute("artworks", artworks);
        model.addAttribute("articles", articles);
        model.addAttribute("videos", videos);
        model.addAttribute("pageName", "Latest");
        return "latest";
    }
}
