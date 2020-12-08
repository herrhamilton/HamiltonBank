package de.othr.sw.hamilton.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import java.math.BigDecimal;

@Controller
public class LoginController {

    @RequestMapping(path = "/login", method = RequestMethod.GET)
    public String showLoginPage() {
        return "login";
    }

    @RequestMapping(path = "/login-submit", method = RequestMethod.POST)
    public String loginUser(Model model) {
        //TODO fix login
        model.addAttribute("amount", new BigDecimal(10));
        return "deposit";
    }
}
