package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;
import de.othr.sw.hamilton.service.IPortfolioService;
import de.othr.sw.hamilton.service.ITransactionService;
import de.othr.sw.hamilton.service.IUserService;
import dev.wobu.stonks.entity.Portfolio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class OverviewController {
    @Value("${appconfig.stonks.url}")
    private String stonksUrl;

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return userService.getCurrentCustomer();
    }

    private final IUserService userService;

    private final IPortfolioService portfolioService;

    private final ITransactionService transactionService;

    public OverviewController(IUserService userService, IPortfolioService portfolioService, ITransactionService transactionService) {
        this.userService = userService;
        this.portfolioService = portfolioService;
        this.transactionService = transactionService;
    }

    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public String showOverview(Model model) {
        List<Transaction> transactions = transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount());
        Map<String, Object> modelAttributes = new HashMap<>();
        Portfolio portfolio;

        modelAttributes += getStonksModelAttributes();
        try {
            portfolio = portfolioService.getStonksPortfolio();
            model.addAttribute("portfolio", portfolio);
        } catch (HttpClientErrorException.Forbidden fe) {
            model.addAttribute("stonksError", "auth");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            model.addAttribute("stonksError", "other");
        }
        model.addAttribute("stonksUrl", stonksUrl);
        model.addAttribute("transactions", transactions);
        ///////////////////////////////7
        model.addAttribute("transactionForm", new TransactionForm());
////////////////////////////////////
        Customer customer = userService.getCurrentCustomer();

        Consulting consulting = customer.getOpenConsulting();

        if (consulting != null) {
            model.addAttribute("hasConsulting", true);
            model.addAttribute("consulting", consulting);
            model.addAttribute("consultingUrl", vociUrl + "/invitation?accessToken=" + consulting.getAccessToken());
        } else {
            model.addAttribute("hasConsulting", false);
            model.addAttribute("consulting", new Consulting());
        }
        return "overview";
    }

    @RequestMapping(path = "/account")
    public String showSettingsPage() {
        return "account";
    }

    @RequestMapping(path = "/account", method = RequestMethod.POST)
    public String submitSettings(@ModelAttribute Customer customer) {
        userService.updateCustomer(customer);
        return "account";
    }

    @RequestMapping(path = "/statement", method = RequestMethod.GET)

    public ResponseEntity<String> getStatement() {
        Customer customer = userService.getCurrentCustomer();
        String csvData = transactionService.generateStatementCsv();
        return csvData == null
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=Kontouebersicht_" + customer.getUsername() + "_HamiltonBank.csv")
                .contentType(MediaType.valueOf("text/csv"))
                .contentLength(csvData.length())
                .body(csvData);
    }
}
