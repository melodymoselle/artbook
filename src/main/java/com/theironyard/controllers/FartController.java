package com.theironyard.controllers;

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
public class FartController {

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

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String forward(){
        return "redirect:/artworks";
    }

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
        model.addAttribute("pageName", "Artworks");
        return "artworks";
    }

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
        model.addAttribute("pageName", "Artists");
        return "artists";
    }

    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(HttpSession session, Model model, int artistId, @RequestParam(defaultValue = "0") int page){
        Artist artist = artistRepo.findOne(artistId);
        if (session.getAttribute(UserController.SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            model.addAttribute(UserController.SESSION_USER, user.getUsername());
            if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
                model.addAttribute("admin", true);
            }
            if (user.isFollowing(artist)){
                model.addAttribute("following", true);
            }
        }
        List<Article> articles = articleRepo.findByArtist(artist);
        List<Artist> similar = artistRepo.findSimilarAndPopulated(artist.getId());
        List<Artwork> artworks = artworkRepo.findByArtist(artist);
        List<Video> videos = videoRepo.findByArtist(artist);
        model.addAttribute("articles", articles);
        model.addAttribute("similar", similar);
        model.addAttribute("artworks", artworks);
        model.addAttribute("videos", videos);
        model.addAttribute("artist", artist);

        model.addAttribute("pageName", artist.getName());
        return "artist";
    }

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
    public String getErrorPage(){
        return "error";
    }

    @RequestMapping(path = "/search", method = RequestMethod.GET)
    public String getSearchResults(Model model, String q, @RequestParam(defaultValue = "0") int page){
        Page<Artist> artists = artistRepo.findByNameContainingIgnoreCase(new PageRequest(page, 9), q);
        for (Artist artist : artists){
            List<Artwork> artworks = artworkRepo.findByArtist(new PageRequest(page, 3), artist).getContent();
            artist.setItems(artworks);
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
        model.addAttribute("pageName", "Search Results");
        return "search";
    }
}
