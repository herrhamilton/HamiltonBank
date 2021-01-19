package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"/home", "/"}, method = RequestMethod.GET)
    public String showStartPage() {
        UserDetails user = userService.getCurrentUser();
        return user == null ? "index" : "overview";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    //TODO groß kleinschreibung oderm acht des spring?
    public String showLoginPage() {
        return "login";
    }
}
