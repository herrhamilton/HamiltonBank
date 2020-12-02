package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegistrationController {

    @Autowired
    private UserService userService;

    @RequestMapping(path="/registration", method = RequestMethod.GET)
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new Customer());
        return "registration";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String registerUser(Model model, @ModelAttribute Customer user) {
        Customer persistedUser = userService.createUser(user);
        model.addAttribute("user", persistedUser);
        return "result";
    }
}
