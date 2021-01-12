package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.TransactionRepository;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class RegistrationController {

    private final UserService userService;

    private final TransactionRepository transactionRepository;

    public RegistrationController(UserService userService, TransactionRepository transactionRepository) {
        this.userService = userService;
        this.transactionRepository = transactionRepository;
    }

    @RequestMapping(path="/registration", method = RequestMethod.GET)
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new Customer());
        return "registration";
    }

    @RequestMapping(path = "/registration-submit", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute Customer user) {
        userService.createUser(user);
        return "login";
    }

    @RequestMapping(path = "/overviewi", method = RequestMethod.GET)
    public String showiOverview(Model model) {
        Customer user = (Customer)userService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(user.getBankAccount(), user.getBankAccount());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "overview";
    }
}
