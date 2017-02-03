package com.theironyard.controllers;

import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
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

@Controller
public class fartController {
    public static final String SESSION_USER = "currentUsername";

    @Autowired
    ArtsyService artsy;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page){
        if (session.getAttribute(SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
            List<Artist> artists = user.getFollowing();
            model.addAttribute("artists", artists);
            model.addAttribute(SESSION_USER, user.getUsername());
            return "home-user";
        }
        Page<Artwork> artworks = artworkRepo.findAllOrderByLikes(new PageRequest(page, 9));
        model.addAttribute("artworks", artworks);

        if(artworks.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artworks.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }

        return "home-no-user";
    }

    @RequestMapping(path = "/discover", method = RequestMethod.GET)
    public String getDiscoverPage(HttpSession session, Model model){
        if (session.getAttribute(SESSION_USER) == null){
            return "/";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        model.addAttribute(SESSION_USER, user.getUsername());

        List<Artist> artists = artistRepo.findAllPopulatedOrderByFollowedBy();
        model.addAttribute("artists", artists);
        return "discover";
    }

    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(HttpSession session, Model model, int artistId, @RequestParam(defaultValue = "0") int page){
        Artist artist = artistRepo.findOne(artistId);
        if (session.getAttribute(SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
            if (user.isFollowing(artist)){
                model.addAttribute("following", true);
            }
            model.addAttribute(SESSION_USER, user.getUsername());
        }

        Page<Artwork> artworks = artworkRepo.findByArtists(new PageRequest(page, 6), artist);
        model.addAttribute("artworks", artworks);
        model.addAttribute("artist", artist);

        if(artworks.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artworks.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("admin", true);
        return "artist";
    }

    @RequestMapping(path = "/artwork", method = RequestMethod.GET)
    public String getArtworkPage(HttpSession session, Model model, int artworkId){
        Artwork artwork = artworkRepo.findOne(artworkId);
        if (session.getAttribute(SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
            if (user.isLiked(artwork)){
                model.addAttribute("liked", true);
            }
            model.addAttribute(SESSION_USER, user.getUsername());
        }
        model.addAttribute("artwork", artwork);
        return "artwork";
    }

}
