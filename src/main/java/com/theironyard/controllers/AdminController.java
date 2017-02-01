package com.theironyard.controllers;

import com.theironyard.entities.Artist;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminController {
    public static final String SESSION_USER = "currentUsername";

    @Autowired
    ArtsyService artsy;

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @RequestMapping(path = "/add-artist", method = RequestMethod.GET)
    public String getAddArtistPage(Model model, @RequestParam(defaultValue = "0") int page){
        Page<Artist> artists = artistRepo.findAll(new PageRequest(page, 10));
        model.addAttribute("artists", artists);

        if(artists.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artists.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }

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

}
