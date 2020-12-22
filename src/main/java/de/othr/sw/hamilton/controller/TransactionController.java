package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class TransactionController {

    private final TransactionService transactionService;

    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @RequestMapping(path = "/deposit", method = RequestMethod.GET)
    public String showDepositPage() {
        return "deposit";
    }

    @RequestMapping(path = "/deposit-submit", method = RequestMethod.POST)
    public String depositMoney(@RequestParam(value = "amount") int amount) {
        //TODO Requestparam zu Model doer so ändern?
        Customer customer = (Customer) userService.getCurrentUser();
        // TODO Input Verification, negative Werte einzahlen
        // BigDecimal statt int?
        //TODO Komponentendiagramm ändern wenn Message Queuing zum Bezahlen benutzt wird (hat nix mit deposit zu tun^^)
        Transaction t = new Transaction(amount, "Einzahlung", customer.getBankAccount());
        transactionService.executeTransaction(t);
        return "deposit";
    }
}
