package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.DepotService;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {

    private final UserService userService;

    private final TransactionService transactionService;

    private final DepotService depotService;

    public HomeController(UserService userService, TransactionService transactionService, DepotService depotService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.depotService = depotService;
    }

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return (Customer) userService.getCurrentUser();
    }

    @ModelAttribute("transactions")
    List<Transaction> transactions() {
        //TODO hacky again.. BEWARE: Wenn Transactions hinzugefügt werden, werden die ggf ned angezeigt weil Model vor Methode berechnet wird
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
    public String showLoginPage() {
        return "login";
    }

    //TODO make Controllers nicer
    @RequestMapping(path="/depot")
    public String showDepotPage(Model model) {
        Customer user = (Customer) userService.getCurrentUser();
        model.addAttribute("taxreport", depotService.getLastYearsTaxReport());
        model.addAttribute("portfolio", depotService.getPortfolio());
        return "depot";
    }

    @RequestMapping(path="/settings")
    public String showSettingsPage(Model model) {
        return "settings";
    }

    @RequestMapping(path="/settings", method = RequestMethod.POST)
    public String submitSettingsPage(@ModelAttribute Customer customer) {
        userService.updateCustomer(customer);
        return "settings";
    }
}
