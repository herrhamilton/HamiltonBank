package de.othr.sw.hamilton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {
    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage(Model model) {
        model.addAttribute("email", "");
        model.addAttribute("password", "");
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String loginUser(Model model, @ModelAttribute String email, @ModelAttribute String password) {
        model.addAttribute("email", email);
        model.addAttribute("password", password);
        // TODO add pw hash
        //TODO login user
        return "main";
    }
}
