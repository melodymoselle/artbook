package com.theironyard.controllers;

import com.sun.org.apache.xpath.internal.operations.Mod;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.services.ArtsyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class fartController {
    @Autowired
    ArtsyService artsy;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @RequestMapping(path = "/add-artist", method = RequestMethod.GET)
    public String getAddArtistPage(){

        return "add-artist";
    }

    @RequestMapping(path = "/add-artist", method = RequestMethod.POST)
    public String addArtistToDB(String artsyArtistId, RedirectAttributes redAtt){
        Artist artist = artsy.getArtistById(artsyArtistId);
        List<Artwork> artworks = artsy.getArtworksByArtist(artist);
        artist.setArtworks(artworks);
        artistRepo.save(artist);
        redAtt.addFlashAttribute("artist", artist);
        redAtt.addFlashAttribute("artworksCount", artworks.size());
        return "redirect:/add-artist";
    }

    @RequestMapping(path = "/artist", method = RequestMethod.GET)
    public String getArtistPage(Model model, int artistId){
        Artist artist = artistRepo.findOne(artistId);
        model.addAttribute("artist", artist);
        return "artist";
    }

    @RequestMapping(path = "/artwork", method = RequestMethod.GET)
    public String getArtworkPage(Model model, int artworkId){
        Artwork artwork = artworkRepo.findOne(artworkId);
        model.addAttribute("artwork", artwork);
        return "artwork";
    }
}
