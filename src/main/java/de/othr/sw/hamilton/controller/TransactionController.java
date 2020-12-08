package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.TransactionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(path = "/deposit", method = RequestMethod.POST)
    public String depositMoney(Model model,
                               @RequestParam(value = "amount") int amount) {
        //TODO get user from principal
        //TODO add description
        Transaction t = new Transaction(amount, "Einzahlung", user.getBankAccount());

        transactionService.persistTransaction(t);

        return "deposit";
    }
}
