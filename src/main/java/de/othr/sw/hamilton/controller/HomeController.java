package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public String showAfterLoginPage() {
        User currentUser = userService.getCurrentUser();

        //TODO roles instead of instanceof check?
        return currentUser instanceof Customer ? "redirect:overview" : "redirect:advisor";
    }

    @RequestMapping(path="/registration", method = RequestMethod.GET)
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new Customer());
        return "registration";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute Customer user) {
        userService.createUser(user);
        return "login";
    }

    @RequestMapping(path="/api/swagger-ui")
    public String showSwagger() {
        return "redirect:/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/";
    }
}
