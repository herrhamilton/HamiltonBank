package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;
import de.othr.sw.hamilton.service.ITransactionService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class TransactionController {

    private final ITransactionService transactionService;

    private final IUserService userService;

    @ModelAttribute("currentCustomer")
    Customer currentCustomer() {
        return userService.getCurrentCustomer();
    }

    @ModelAttribute("transactions")
    List<Transaction> transactions() {
        return transactionService.findTransactionsForBankAccount(currentCustomer().getBankAccount());
    }

    public TransactionController(ITransactionService transactionService, IUserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @RequestMapping(path = "/deposit", method = RequestMethod.POST)
    public String depositMoney(@RequestParam(value = "amount") String amountString) {
        BigDecimal amount = transactionService.getAmountFromString(amountString);
        transactionService.depositMoney(amount);
        return "redirect:/overview";
    }

    @RequestMapping(path = "/transaction", method = RequestMethod.POST)
    public String transactionMoney(@ModelAttribute TransactionForm transactionForm, Model model) {
        try {
            transactionService.sendTransaction(transactionForm);
            return "redirect:/overview";
        } catch (UsernameNotFoundException ex) {
            model.addAttribute("userNotFound", true);
            model.addAttribute("transactionForm", transactionForm);
            return "transaction";
        }
    }
}
