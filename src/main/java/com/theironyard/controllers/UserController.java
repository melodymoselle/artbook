package com.theironyard.controllers;

import com.theironyard.commands.LoginCommand;
import com.theironyard.commands.RegisterCommand;
import com.theironyard.entities.Artist;
import com.theironyard.entities.User;
import com.theironyard.repositories.ArtistRepository;
import com.theironyard.repositories.UserRepository;
import com.theironyard.utilities.PasswordStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    public static final String SESSION_USER = "currentUsername";

    @Autowired
    UserRepository userRepo;

    @Autowired
    ArtistRepository artistRepo;

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, LoginCommand command, RedirectAttributes redAtt) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {
        User user = userRepo.findByUsername(command.getUsername());
        if (user == null || PasswordStorage.verifyPassword(command.getPassword(), user.getPassword())){
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
            redAtt.addFlashAttribute("message", "That username is taken");
            return "redirect:/register";
        }
        user = new User(command.getUsername(), PasswordStorage.createHash(command.getPassword()));
        session.setAttribute(SESSION_USER, user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public String logout(HttpSession session){
        session.invalidate();
        return "redirect:/";
    }

    @RequestMapping(path = "/follow", method = RequestMethod.GET)
    public String followArtist(HttpSession session, int artistId){
        if (session.getAttribute(SESSION_USER) == null){
            return "/";
        }
        User user = userRepo.findByUsername(SESSION_USER);
        Artist artist = artistRepo.findOne(artistId);
        user.addFollowing(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.GET)
    public String unfollowArtist(HttpSession session, int artistId){
        if (session.getAttribute(SESSION_USER) == null){
            return "/";
        }
        User user = userRepo.findByUsername(SESSION_USER);
        Artist artist = artistRepo.findOne(artistId);
        user.deleteFollowing(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }
}
