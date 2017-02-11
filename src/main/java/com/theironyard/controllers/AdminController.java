package com.theironyard.controllers;

import com.theironyard.entities.*;
import com.theironyard.repositories.*;
import com.theironyard.services.ArtsyService;
import com.theironyard.services.GoogleCSEService;
import com.theironyard.services.WikipediaService;
import com.theironyard.services.YoutubeService;
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
public class AdminController {

    @Autowired
    ArtsyService artsy;

    @Autowired
    GoogleCSEService google;

    @Autowired
    WikipediaService wiki;

    @Autowired
    YoutubeService youtube;

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

    @RequestMapping(path = "/add-artist", method = RequestMethod.GET)
    public String getAddArtistPage(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page, RedirectAttributes redAtt){
        if (session.getAttribute(UserController.SESSION_USER) == null){
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
        if (user.getPrivileges() != User.Rights.ADMINISTRATOR) {
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        Page<Artist> artists = artistRepo.findAll(new PageRequest(page, 9));
        model.addAttribute("artists", artists);

        if(artists.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artists.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("admin", true);
        model.addAttribute("pageName", "Add Artist");

        return "add-artist";
    }

    @RequestMapping(path = "/add-artist", method = RequestMethod.POST)
    public String addArtistToDB(HttpSession session, Model model, String artsyArtistId, RedirectAttributes redAtt){
        if (session.getAttribute(UserController.SESSION_USER) == null){
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
        if (user.getPrivileges() != User.Rights.ADMINISTRATOR) {
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        Artist artist = artistRepo.findByArtsyArtistId(artsyArtistId);
        if (artist == null) {
            artist = artsy.getArtistByArtsyId(artsyArtistId);
            artistRepo.save(artist);
        }

        return "redirect:/artist?artistId=" + artist.getId();
    }

    @RequestMapping(path = "/load-artist", method = RequestMethod.GET)
    public String loadArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        String message = validateUser(session);
        if (message != null){
            redAtt.addAttribute("message", message);
            return "redirect:/error";
        }
        Artist artist = artistRepo.findOne(artistId);
        artist = getSaveArtistData(artist);

        return "redirect:/artist?artistId=" + artist.getId();
    }

    private Artist getSaveArtistData(Artist artist){
        artist.setLoaded(true);
        String summary = wiki.getWikiIntro(artist);
        List<Artwork> artworks = artsy.getArtworksByArtist(artist);
        List<Artist> similarArtists = artsy.getSimilarToByArtist(artist);
        List<Article> articles = google.getArticlesByArtist(artist);
        List<Video> videos = youtube.getYoutubeVideos(artist);
        artist.setSummary(summary);
        artworkRepo.save(artworks);
        artist.getItems().addAll(artworks);
        artistRepo.save(similarArtists);
        artist.getSimilarTo().addAll(similarArtists);
        articleRepo.save(articles);
        artist.getItems().addAll(articles);
        videoRepo.save(videos);
        artist.getItems().addAll(videos);
        artistRepo.save(artist);
        return artist;
    }

    private String validateUser(HttpSession session){
        String message = null;
        if (session.getAttribute(UserController.SESSION_USER) == null){
            message = "You do not have access for that action.";
        }
        else {
            User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
            if (user.getPrivileges() != User.Rights.ADMINISTRATOR) {
                message = "You do not have access for that action.";
            }
        }
        return message;
    }
}
