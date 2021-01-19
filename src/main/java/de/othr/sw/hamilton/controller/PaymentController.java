package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.service.PaymentService;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
//TODO move /payment/ into "root" path
public class PaymentController {

    private final UserService userService;

    private final PaymentService paymentService;

    private final TransactionService transactionService;

    public PaymentController(UserService userService, PaymentService paymentService, TransactionService transactionService) {
        this.userService = userService;
        this.paymentService = paymentService;
        this.transactionService = transactionService;
    }

    @RequestMapping(path = "/api/payment/create", method = RequestMethod.POST)
    @ResponseBody
    public Payment createPayment(@RequestBody Payment payment) {
        return paymentService.createPayment(payment);
    }

    @RequestMapping(path = "/api/payment/check/{paymentId}")
    @ResponseBody
    public Payment checkPayment(@PathVariable("paymentId") UUID paymentId) {
        //TODO maybe auth, sodass nicht jeder alle Payments checken kann?
        return paymentService.findPayment(paymentId);
    }

    @RequestMapping(path = "/payment/{paymentId}")
    public String showPaymentPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        //TODO Exception Handling hier und überall
        //TODO Unit Tests
        Payment payment = paymentService.findPayment(paymentId);
        Customer user = (Customer) userService.getCurrentUser();

        model.addAttribute("user", user);
        model.addAttribute("payment", payment);
        return "payment";
    }

    @RequestMapping(path = "/payment/{paymentId}/fulfill", method = RequestMethod.POST)
    public String fulfillPaymentAndShowOverviewPage(@PathVariable("paymentId") UUID paymentId, Model model) {
        Customer user = (Customer) userService.getCurrentUser();
        Payment payment = paymentService.findPayment(paymentId);

        paymentService.fulfillPayment(payment, user);

        //TODO move into method or sth
        List<Transaction> transactions = transactionService.findTransactionsForBankAccount(user.getBankAccount());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "overview";
    }
}