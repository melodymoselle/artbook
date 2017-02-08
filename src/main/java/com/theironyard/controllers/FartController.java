package com.theironyard.controllers;

import com.theironyard.entities.Article;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArticleRepository;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.services.ArtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

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
        Set<Artist> similar = artistRepo.findSimilarAndPopulated(artist.getId());
        List<Artwork> artworks = artworkRepo.findByArtist(artist);
// Page<Artwork> artworks = artworkRepo.findByArtist(new PageRequest(page, 6), artist);
        model.addAttribute("articles", articles);
        model.addAttribute("similar", similar);
        model.addAttribute("artworks", artworks);
        model.addAttribute("artist", artist);

//        if(artworks.hasPrevious()){
//            model.addAttribute("previous", true);
//            model.addAttribute("prevPageNum", page - 1);
//        }
//        if(artworks.hasNext()){
//            model.addAttribute("next", true);
//            model.addAttribute("nextPageNum", page + 1);
//        }
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

}
