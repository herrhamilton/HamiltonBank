package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @RequestMapping("/")
    public String showStartPage(Model model) {
        model.addAttribute("today", new Date().toString());
        return "index";
    }

    /*@RequestMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }*/

    @RequestMapping("/index")
    public String showIndexPage(Model model) {
        return "index";
    }

    @GetMapping(path="/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new Customer());
        return "register";
    }

    @PostMapping(path = "/register")
    public String registerUser(Model model, @ModelAttribute Customer user) {
        Customer persistedUser = userService.createUser(user);
        model.addAttribute("user", persistedUser);
        return "result";
    }
}
