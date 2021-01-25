package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.management.openmbean.KeyAlreadyExistsException;

@Controller
public class HomeController {

    private final UserService userService;

    public HomeController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String showStartPage() {
        return "index";
    }

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(path = "/login/failed", method = RequestMethod.GET)
    public String showLoginFailedPage(Model model) {
        model.addAttribute("failed", true);
        return "login";
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String showAfterLoginPage() {
        User currentUser = userService.getCurrentUser();
        return currentUser instanceof Customer ? "redirect:overview" : "redirect:advisor";
    }

    @RequestMapping(path="/registration", method = RequestMethod.GET)
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new Customer());
        return "registration";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute Customer user, Model model) {
        try {
            userService.createUser(user);
        } catch(KeyAlreadyExistsException ex) {
            model.addAttribute("userExists", true);
            model.addAttribute("user", user);
            return "registration";
        }
        return "login";
    }

    @RequestMapping(path="/api/swagger-ui")
    public String showSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/";
    }
}
