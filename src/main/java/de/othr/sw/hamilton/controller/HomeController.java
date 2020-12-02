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

    @RequestMapping(value = { "/home", "/" })
    public String showStartPage(Model model) {
        model.addAttribute("today", new Date().toString());
        return "index";
    }
}
