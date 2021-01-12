package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.TransactionRepository;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    private final TransactionRepository transactionRepository;

    private final UserService userService;

    private final BankAccountRepository bankAccountRepository;

    public TransactionController(TransactionService transactionService, TransactionRepository transactionRepository, UserService userService, BankAccountRepository bankAccountRepository) {
        this.transactionService = transactionService;
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.bankAccountRepository = bankAccountRepository;
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
        // BigDecimal statt int?
        //TODO Komponentendiagramm ändern wenn Message Queuing zum Bezahlen benutzt wird (hat nix mit deposit zu tun^^)
        Transaction t = new Transaction(amount, "Einzahlung", customer.getBankAccount());
        transactionService.executeTransaction(t);
        return showOverview(model);
    }

    @RequestMapping(path = "/overview", method = RequestMethod.GET)
    public String showOverview(Model model) {
        Customer user = (Customer)userService.getCurrentUser();
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(user.getBankAccount(), user.getBankAccount());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "overview";
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public String showTransactionPage(Model model) {
        model.addAttribute("transaction", new Transaction());
        //TODO less hacky
        model.addAttribute("toAccId", new String());
        return "transfer";
    }

    @RequestMapping(path = "/transfer-submit", method = RequestMethod.POST)
    public String transferMoney(@ModelAttribute Transaction transaction, Model model) {
        //TODO Requestparam zu Model doer so ändern?
        BankAccount from = ((Customer) userService.getCurrentUser()).getBankAccount();
        BankAccount to = (bankAccountRepository.findById(transaction.getToAccId())).get();
        transaction.setFromAccount(from);
        transaction.setToAccount(to);
        transaction.setDate(new Date());
        // TODO Input Verification, negative Werte einzahlen
        // BigDecimal statt int?
        //TODO Komponentendiagramm ändern wenn Message Queuing zum Bezahlen benutzt wird (hat nix mit deposit zu tun^^)
        transactionService.executeTransaction(transaction);
        return showOverview(model);
    }
}
