package com.theironyard.controllers;

import com.theironyard.entities.Artist;
import com.theironyard.entities.User;
import com.theironyard.entities.Video;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.repositories.VideoRepository;
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
    UserRepository userRepo;

    @Autowired
    WikipediaService wiki;

    @Autowired
    YoutubeService youtube;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

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
        Artist artist = artsy.getSaveArtistById(artsyArtistId);
        model.addAttribute("pageName", "Add Artist");
        return "redirect:/artist?artistId=" + artist.getId();
    }

    @RequestMapping(path = "/load-artist", method = RequestMethod.GET)
    public String loadArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        if (session.getAttribute(UserController.SESSION_USER) == null){
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(UserController.SESSION_USER).toString());
        if (user.getPrivileges() != User.Rights.ADMINISTRATOR) {
            redAtt.addAttribute("message", "You do not have access for that action.");
            return "redirect:/error";
        }
        Artist artist = artistRepo.findOne(artistId);
        artist = artsy.getSaveArtworksByArtist(artist);
        artist = artsy.getSaveSimilarToByArtist(artist);
        artist = google.getArticlesByArtist(artist);
        artist.setSummary(wiki.getWikiIntro(artist));
        List<Video> videos = youtube.getYoutubeVideos(artist);
        videoRepo.save(videos);
        artist.getItems().addAll(videos);
        artistRepo.save(artist);
        return "redirect:/artist?artistId=" + artistId;
    }

    @RequestMapping(path = "/load-artworks", method = RequestMethod.GET)
    public String getAddArtworksToArtist(int artistId){
        Artist artist = artistRepo.findOne(artistId);
        artist = artsy.getSaveArtworksByArtist(artist);
        return "redirect:/artist?artistId="+artistId;
    }

    @RequestMapping(path = "/load-similar-artists", method = RequestMethod.GET)
    public String getAddSimilarArtistsToArtist(int artistId){
        Artist artist = artistRepo.findOne(artistId);
        artist = artsy.getSaveSimilarToByArtist(artist);
        return "redirect:/artist?artistId="+artistId;
    }

}
