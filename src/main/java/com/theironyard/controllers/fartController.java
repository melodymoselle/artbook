package com.theironyard.controllers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
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

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String getHome(HttpSession session, Model model){
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        if (user != null){
            model.addAttribute("currentUser", user);
            return "home";
        }
        return "index";
    }


    @RequestMapping(path = "/add-artist", method = RequestMethod.GET)
    public String getAddArtistPage(Model model){
        List<Artist> artists = artistRepo.findAll();
        model.addAttribute("artists", artists);
        return "add-artist";
    }

    @RequestMapping(path = "/add-artist", method = RequestMethod.POST)
    public String addArtistToDB(String artsyArtistId, RedirectAttributes redAtt){
        Artist artist = artsy.getSaveArtistById(artsyArtistId);
        artist = artsy.getSaveArtworksByArtist(artist);
        artist = artsy.getSaveSimilarToByArtist(artist);
        redAtt.addFlashAttribute("artist", artist);
        return "redirect:/add-artist";
    }

    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(Model model, int artistId){
        Artist artist = artistRepo.findOne(artistId);
        model.addAttribute("artist", artist);
        return "artist";
    }

    @RequestMapping(path = "/get-artworks", method = RequestMethod.GET)
    public String getAddArtworksToArtist(int artistId){
        Artist artist = artistRepo.findOne(artistId);
        artist = artsy.getSaveArtworksByArtist(artist);
        return "redirect:/artist?artistId="+artistId;
    }

    @RequestMapping(path = "/get-similar-artists", method = RequestMethod.GET)
    public String getAddSimilarArtistsToArtist(int artistId){
        Artist artist = artistRepo.findOne(artistId);
        artist = artsy.getSaveSimilarToByArtist(artist);
        return "redirect:/artist?artistId="+artistId;
    }
    @RequestMapping(path = "/artwork", method = RequestMethod.GET)
    public String getArtworkPage(Model model, int artworkId){
        Artwork artwork = artworkRepo.findOne(artworkId);
        model.addAttribute("artwork", artwork);
        return "artwork";
    }
}
