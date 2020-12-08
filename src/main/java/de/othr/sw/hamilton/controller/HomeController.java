package de.othr.sw.hamilton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {

    @RequestMapping(value = { "/home", "/" })
    public String showStartPage() {
        return "index";
    }
}
