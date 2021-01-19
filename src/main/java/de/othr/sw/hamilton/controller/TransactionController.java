package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.TransactionRepository;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    private final UserService userService;

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return (Customer) userService.getCurrentUser();
    }

    @ModelAttribute("transactions")
    List<Transaction> transactions() {
        return transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount());
    }

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @RequestMapping(path = "/deposit", method = RequestMethod.GET)
    public String showDepositPage() {
        return "deposit";
    }

    @RequestMapping(path = "/deposit-submit", method = RequestMethod.POST)
    public String depositMoney(@RequestParam(value = "amount") int amount, Model model) {
        //TODO Requestparam zu Model doer so ändern?
        Customer customer = (Customer) userService.getCurrentUser();
        // TODO Input Verification, negative Werte einzahlen
        //TODO Komponentendiagramm ändern wenn Message Queuing zum Bezahlen benutzt wird (hat nix mit deposit zu tun^^)
        Transaction t = new Transaction(BigDecimal.valueOf(amount), "Einzahlung", customer.getBankAccount());
        transactionService.executeTransaction(t);
        return showOverview(model);
    }

    //TODO in fact not needed anymore, but check if this will change the url path
    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public String showOverview(Model model) {
        return "overview";
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public String showTransactionPage(Model model) {
        model.addAttribute("transaction", new Transaction());
        //TODO less hacky
        model.addAttribute("toAccId", "");
        return "transfer";
    }

    @RequestMapping(path = "/transfer-submit", method = RequestMethod.POST)
    public String transferMoney(@ModelAttribute Transaction transaction, Model model) {
        transactionService.transferMoney(transaction);
        return showOverview(model);
    }
}
