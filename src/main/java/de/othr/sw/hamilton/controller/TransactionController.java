package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(path = "/deposit", method = RequestMethod.POST)
    public String depositMoney(Model model,
                               @RequestParam(value = "amount") int amount,
                               @ModelAttribute Customer user) {
        //TODO add description
        Transaction t = new Transaction(amount, "Einzahlung", user.getBankAccount());
        transactionService.persistTransaction(t);

        //TODO this one needed? Could/should be inside model already
        model.addAttribute("user", user);
        return "deposit";
    }
}
