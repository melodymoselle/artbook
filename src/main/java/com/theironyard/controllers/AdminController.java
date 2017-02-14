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
import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * Gets all artists from DB. Adds to model. Verifies user is has admin privileges.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param page Current page number for database paging
     * @param redAtt RedirectAttributes for invalid request
     * @return model and 'add-artist' view
     */
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

    /**
     * Calls ArtsyService with 'artsyArtistId'. Creates Artist object with response data.
     * Saves to DB. And redirects to the artist's page.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param artsyArtistId id of artist in the Artsy API database
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects to /artist page
     */
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

    /**
     * Calls getArtistData. Saves artist with new data. Redirects back to artist page.
     *
     * @param session Current HttpSession
     * @param artistId id of artist in local db
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects to /artist page
     */
    @RequestMapping(path = "/load-artist", method = RequestMethod.GET)
    public String loadArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        String message = validateUser(session);
        if (message != null){
            redAtt.addAttribute("message", message);
            return "redirect:/error";
        }
        Artist artist = artistRepo.findOne(artistId);
        artist = getArtistData(artist);
        artistRepo.save(artist);

        return "redirect:/artist?artistId=" + artist.getId();
    }

    @RequestMapping(path = "/update-artist", method = RequestMethod.GET)
    public String updateArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        String message = validateUser(session);
        if (message != null){
            redAtt.addAttribute("message", message);
            return "redirect:/error";
        }
        Artist artist = artistRepo.findOne(artistId);
        List<Article> articles = google.getArticlesByArtist(artist);
        List<Video> videos = youtube.getYoutubeVideos(artist);
        artist.getItems().addAll(articles);
        artist.getItems().addAll(videos);
        artist.setUpdatedAt(LocalDateTime.now());

        return "redirect:/artist?artistId=" + artist.getId();
    }


    /**
     * Calls APIs with artist object and adds related data objects to artist. Unsaved.
     *
     * @param artist Artist object
     * @return Artist object with new data added
     */
    private Artist getArtistData(Artist artist){
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
        return artist;
    }

    /**
     * Gets user from session and validates user access.
     *
     * @param session Current HttpSession
     * @return String
     */
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
