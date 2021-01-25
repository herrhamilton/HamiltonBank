package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.PortfolioService;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import dev.wobu.stonks.entity.Portfolio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Controller
public class OverviewController {
    @Value("${appconfig.stonks.url}")
    private String stonksUrl;

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return userService.getCurrentCustomer();
    }

    private final UserService userService;

    private final PortfolioService portfolioService;

    private final TransactionService transactionService;

    public OverviewController(UserService userService, PortfolioService portfolioService, TransactionService transactionService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
    }

    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public String showOverview(Model model) {
        List<Transaction> transactions = transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount());
        Portfolio portfolio = null;

        try {
            portfolio = portfolioService.getStonksPortfolio();
            model.addAttribute("portfolio", portfolio);
        } catch(HttpClientErrorException.Forbidden fe) {
            model.addAttribute("stonksError", "auth");
        } catch(Exception e) {
            model.addAttribute("stonksError", "other");
        }

        model.addAttribute("stonksUrl", stonksUrl);
        model.addAttribute("transactions", transactions);
        return "overview";
    }

    @RequestMapping(path = "/settings")
    public String showSettingsPage() {
        return "settings";
    }

    @RequestMapping(path = "/settings", method = RequestMethod.POST)
    public String submitSettings(@ModelAttribute Customer customer) {
        userService.updateCustomer(customer);
        return "settings";
    }

    @RequestMapping(path = "/statement", method = RequestMethod.GET)

    public ResponseEntity<String> getStatement(@ModelAttribute Customer customer) {

        String csvData = transactionService.generateStatementCsv();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Kontoübersicht_HamiltonBank.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .contentLength(csvData.length())
                .body(csvData);
    }
}
