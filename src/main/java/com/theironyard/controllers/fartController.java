package com.theironyard.controllers;

import com.theironyard.entities.Artist;
import com.theironyard.entities.ArtsyImage;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtsyImageRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.services.ArtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @Autowired
    ArtsyImageRepository artsyImgRepo;

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model){
        if (session.getAttribute(SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
            List<Artist> artists = user.getFollowing();
            model.addAttribute("artists", artists);
            return "user-home";
        }
        List<Artist> artists = artistRepo.findAllPopulatedOrderByFollowedBy();
        model.addAttribute("artists", artists);
        return "no-user-home";
    }

    @RequestMapping(path = "/discover", method = RequestMethod.GET)
    public String getDiscoverPage(HttpSession session, Model model){
        if (session.getAttribute(SESSION_USER) == null){
            return "/";
        }

        List<Artist> artists = artistRepo.findAllPopulatedOrderByFollowedBy();
        model.addAttribute("artists", artists);
        return "discover";
    }

    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(HttpSession session, Model model, int artistId){
        Artist artist = artistRepo.findOne(artistId);
        if (session.getAttribute(SESSION_USER) != null){
            User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
            if (user.isFollowing(artist)){
                model.addAttribute("following", true);
            }
        }
        ArtsyImage image = artsyImgRepo.findByVersionAndArtist("large", artist);
        model.addAttribute("image", image);
        model.addAttribute("artist", artist);
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
        }
        ArtsyImage image = artsyImgRepo.findByVersionAndArtwork("large", artwork);
        model.addAttribute("image", image);
        model.addAttribute("artwork", artwork);
        return "artwork";
    }

}
