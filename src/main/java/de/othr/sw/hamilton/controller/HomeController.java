package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;

    private final TransactionService transactionService;

    public HomeController(UserService userService, TransactionService transactionService) {
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return (Customer) userService.getCurrentUser();
    }

    @ModelAttribute("transactions")
    List<Transaction> transactions() {
        //TODO hacky again..
        return currentCustomer() != null
                ? transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount())
                : new ArrayList<>();
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
