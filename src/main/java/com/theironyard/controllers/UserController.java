package com.theironyard.controllers;

import com.theironyard.commands.LoginCommand;
import com.theironyard.commands.RegisterCommand;
import com.theironyard.entities.Artist;
import com.theironyard.entities.Artwork;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.ArtworkRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utitilties.PasswordStorage;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {
    public static final String SESSION_USER = "currentUsername";

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @Autowired
    ArtworkRepository artworkRepo;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLogin(){
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, LoginCommand command, RedirectAttributes redAtt) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {
        User user = userRepo.findByUsername(command.getUsername());
        if (user == null || !PasswordStorage.verifyPassword(command.getPassword(), user.getPassword())){
            redAtt.addFlashAttribute("message", "Invalid Username/Password");
            return "redirect:/login";
        }
        session.setAttribute(SESSION_USER, user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(HttpSession session, RegisterCommand command, RedirectAttributes redAtt) throws PasswordStorage.CannotPerformOperationException {
        User user = userRepo.findByUsername(command.getUsername());
        if (user != null){
            redAtt.addFlashAttribute("message", "That username is taken.");
            return "redirect:/login";
        }
        user = new User(command.getUsername(), PasswordStorage.createHash(command.getPassword()));
        userRepo.save(user);
        session.setAttribute(SESSION_USER, user.getUsername());
        return "redirect:/discover";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/discover", method = RequestMethod.GET)
    public String getDiscoverPage(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page, RedirectAttributes redAtt){
        Page<Artist> artists = artistRepo.findAllOrderByFollowers(new PageRequest(page, 9));
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        model.addAttribute(SESSION_USER, user.getUsername());
        if (user.getPrivileges() == User.rights.ADMINISTRATOR) {
            model.addAttribute("admin", true);
        }
        List<Artist> following = user.getFollowing();
        if (following.size() > 0) {
            artists = artistRepo.findSimilarFromFollowing(new PageRequest(page, 9), following);
        }
        model.addAttribute("artists", artists);
        model.addAttribute("pageName", "Discover");
        return "discover";
    }

    @RequestMapping(path = "/follow", method = RequestMethod.GET)
    public String followArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artist artist = artistRepo.findOne(artistId);
        user.addFollowing(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.GET)
    public String unfollowArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artist artist = artistRepo.findOne(artistId);
        user.deleteFollowing(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }

    @RequestMapping(path = "/like", method = RequestMethod.GET)
    public String likeArtwork(HttpSession session, int artworkId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artwork artwork = artworkRepo.findOne(artworkId);
        user.addLiked(artwork);
        userRepo.save(user);
        return "redirect:/artwork?artworkId="+artworkId;
    }

    @RequestMapping(path = "/unlike", method = RequestMethod.GET)
    public String unlikeArtwork(HttpSession session, int artworkId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artwork artwork = artworkRepo.findOne(artworkId);
        user.deleteLiked(artwork);
        userRepo.save(user);
        return "redirect:/artwork?artworkId="+artworkId;
    }
}
