package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.DepotService;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class OverviewController {

    private final UserService userService;

    private final DepotService depotService;

    private final TransactionService transactionService;

    public OverviewController(UserService userService, DepotService depotService, TransactionService transactionService) {
        this.userService = userService;
        this.depotService = depotService;
        this.transactionService = transactionService;
    }

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return  userService.getCurrentCustomer();
    }

    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public String showOverview(Model model) {
        List<Transaction> transactions = transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount());

        model.addAttribute("transactions", transactions);
        return "overview";
    }

    @RequestMapping(path="/depot")
    public String showDepotPage(Model model) {
        model.addAttribute("tax" +
                "report", depotService.getLastYearsTaxReport());
        model.addAttribute("portfolio", depotService.getPortfolio());
        return "depot";
    }

    @RequestMapping(path="/settings")
    public String showSettingsPage() {
        return "settings";
    }

    @RequestMapping(path="/settings", method = RequestMethod.POST)
    public String submitSettings(@ModelAttribute Customer customer) {
        userService.updateCustomer(customer);
        return "settings";
    }
}
