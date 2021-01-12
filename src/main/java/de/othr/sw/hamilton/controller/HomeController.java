package de.othr.sw.hamilton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping(value = { "/home", "/" }, method = RequestMethod.GET)
    public String showStartPage() {
        return "index";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    //TODO groß kleinschreibung oderm acht des spring?
    public String showLoginPage() {
        return "login";
    }
}
