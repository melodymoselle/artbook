package com.theironyard.controllers;

import com.theironyard.commands.LoginCommand;
import com.theironyard.commands.RegisterCommand;
import com.theironyard.entities.*;
import com.theironyard.repositories.*;
import com.theironyard.utitilties.PasswordStorage;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Autowired
    ArticleRepository articleRepo;

    @Autowired
    VideoRepository videoRepo;

    @Autowired
    ItemRepository itemRepo;

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String getLogin( Model model){
        model.addAttribute("pageName", "Login/Register");
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login(HttpSession session, LoginCommand command, RedirectAttributes redAtt) throws PasswordStorage.InvalidHashException, PasswordStorage.CannotPerformOperationException {
        User user = userRepo.findByUsername(command.getUsername());
        if (user == null || !PasswordStorage.verifyPassword(command.getPassword(), user.getPassword())){
            redAtt.addFlashAttribute("message", "Invalid Username/Password");
            return "redirect:/login";
        }
        user.setPrevLogin(user.getCurrLogin());
        user.setCurrLogin(LocalDateTime.now());
        session.setAttribute(SESSION_USER, user.getUsername());
        return "redirect:/";
    }

    @RequestMapping(path = "/register", method = RequestMethod.POST)
    public String register(HttpSession session, RegisterCommand command, RedirectAttributes redAtt) throws PasswordStorage.CannotPerformOperationException {
        User user = userRepo.findByUsername(command.getUsername());
        if (user != null){
            redAtt.addFlashAttribute("message", "That username is taken.");
            return "redirect:/login#register";
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

    /**
     * Gets 'user' from session. If 'user' is following 'artists' adds those artists to model.
     * Else, adds all 'artists' ordered by 'followers' to the model.
     *
     * @param session Current HttpSession
     * @param model Model to be passed to the view
     * @param page Current page number for database paging
     * @param redAtt RedirectAttributes for invalid request
     * @return model and 'discover' view
     */
    @RequestMapping(path = "/discover", method = RequestMethod.GET)
    public String getDiscoverPage(HttpSession session, Model model, @RequestParam(defaultValue = "0") int page, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        model.addAttribute(SESSION_USER, user.getUsername());
        if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
            model.addAttribute("admin", true);
        }
        Page<Artist> artists = artistRepo.findAllOrderByFollowers(new PageRequest(page, 9));
        List<Artist> suggestions = new ArrayList<>();
        if (user.getFollowing().size() > 0) {
            artists = artistRepo.findSimilarFromFollowing(new PageRequest(page, 9), user.getFollowing());
        }
        if(artists.hasPrevious()){
            model.addAttribute("previous", true);
            model.addAttribute("prevPageNum", page - 1);
        }
        if(artists.hasNext()){
            model.addAttribute("next", true);
            model.addAttribute("nextPageNum", page + 1);
        }
        model.addAttribute("artists", artists);
        model.addAttribute("pagingStub", "discover");
        model.addAttribute("pageName", "Discover");
        return "discover";
    }

    /**
     * Gets current 'user' from session and 'artist' from params. Adds 'artist' to 'user's
     * following.
     *
     * @param session Current HttpSession
     * @param artistId 'id' of current 'artist'
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects back to same page
     */
    @RequestMapping(path = "/follow", method = RequestMethod.GET)
    public String followArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artist artist = artistRepo.findOne(artistId);
        user.getFollowing().add(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }

    /**
     * Gets current 'user' from session and 'artist' from params. Removes 'artist' from 'user's
     * following.
     *
     * @param session Current HttpSession
     * @param artistId 'id' of current 'artist'
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects back to same page
     */
    @RequestMapping(path = "/unfollow", method = RequestMethod.GET)
    public String unfollowArtist(HttpSession session, int artistId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Artist artist = artistRepo.findOne(artistId);
        user.getFollowing().remove(artist);
        userRepo.save(user);
        return "redirect:/artist?artistId="+artistId;
    }

    /**
     * Gets current 'user' from session and 'item' from params. Adds 'item' to 'user's
     * likes.
     *
     * @param session Current HttpSession
     * @param itemId 'id' of current 'item'
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects back to same page
     */
    @RequestMapping(path = "/like", method = RequestMethod.GET)
    public String likeArtwork(HttpSession session, HttpServletRequest request, int itemId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Item item = itemRepo.findOne(itemId);
        user.getLiked().add(item);
        item.getLikedBy().add(user);
        userRepo.save(user);
        itemRepo.save(item);
        String redirect;
        try {
            URI referrer = new URI(request.getHeader("referer"));
            redirect = referrer.getPath() +"?"+ referrer.getQuery();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            redirect = "/";
        }
        return "redirect:"+redirect;
    }

    /**
     * Gets current 'user' from session and 'item' from params. Removes 'item' from 'user's
     * likes.
     *
     * @param session Current HttpSession
     * @param itemId 'id' of current 'item'
     * @param redAtt RedirectAttributes for invalid request
     * @return redirects back to same page
     */
    @RequestMapping(path = "/unlike", method = RequestMethod.GET)
    public String unlikeArtwork(HttpSession session, HttpServletRequest request, int itemId, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        Item item = itemRepo.findOne(itemId);
        user.getLiked().remove(item);
        item.getLikedBy().remove(user);
        userRepo.save(user);
        itemRepo.save(item);
        String redirect;
        try {
            URI referrer = new URI(request.getHeader("referer"));
            redirect = referrer.getPath() +"?"+ referrer.getQuery();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            redirect = "/";
        }
        return "redirect:"+redirect;
    }

    @RequestMapping(path = "/collection", method = RequestMethod.GET)
    public String getCollectionPage(HttpSession session, Model model, RedirectAttributes redAtt){
        if (session.getAttribute(SESSION_USER) == null){
            redAtt.addAttribute("message", "You need to be signed for that action.");
            return "redirect:/error";
        }
        User user = userRepo.findByUsername(session.getAttribute(SESSION_USER).toString());
        model.addAttribute(UserController.SESSION_USER, user.getUsername());
        if (user.getPrivileges() == User.Rights.ADMINISTRATOR) {
            model.addAttribute("admin", true);
        }
        List<Item> items = itemRepo.findAllByLikedBy(user);
        List<Artwork> artworks = artworkRepo.findAllByLikedBy(user);
        List<Article> articles = articleRepo.findAllByLikedBy(user);
        List<Video> videos = videoRepo.findAllByLikedBy(user);

        model.addAttribute("items", items);
        model.addAttribute("artworks", artworks);
        model.addAttribute("articles", articles);
        model.addAttribute("videos", videos);
        model.addAttribute("pageName", "My Collection");
        return "collection";
    }
}
