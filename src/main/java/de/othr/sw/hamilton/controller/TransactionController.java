package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;
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
        return  userService.getCurrentCustomer();
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

    @RequestMapping(path = "/deposit", method = RequestMethod.POST)
    public String depositMoney(@RequestParam(value = "amount") int amount) {
        // TODO Input Verification, negative Werte einzahlen
        transactionService.depositMoney(amount);
        return "redirect:/overview";
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.GET)
    public String showTransactionPage(Model model) {
        model.addAttribute("transactionForm", new TransactionForm());
        return "transfer";
    }

    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public String transferMoney(@ModelAttribute TransactionForm transactionForm ) {
        //TODO test this and exception handling for input
        transactionService.sendTransaction(transactionForm);
        return "redirect:/overview";
    }
}
