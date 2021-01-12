package de.othr.sw.hamilton.controller;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.PaymentRepository;
import de.othr.sw.hamilton.repository.TransactionRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import de.othr.sw.hamilton.service.TransactionService;
import de.othr.sw.hamilton.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Controller
public class PaymentController {

    private UserService userService;

    private TransactionService transactionService;

    private UserRepository userRepository;

    private PaymentRepository paymentRepository;

    private TransactionRepository transactionRepository;

    public PaymentController(UserRepository userRepository, UserService userService, TransactionService transactionService, PaymentRepository requestRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.transactionService = transactionService;
        this.paymentRepository = requestRepository;
        this.transactionRepository = transactionRepository;
    }

    //@Operation(summary = "Get a new request where the specified customer asks for a specified amount")
    @RequestMapping(path = "/request/{customerId}/{amount}", method = RequestMethod.GET)
    public Payment createPayment(
            //@Parameter(description = "account to send the money to")
            @PathVariable("customerId") Long customerId,
            @PathVariable("amount") int amount
            //@RequestHeader(value = AUTH_SECRET_API_KEY_HEADER, required = true) String secretKey) {
    ) {

        try {
            //TODO add description to payment
            Customer receiver = (Customer) (userRepository.findById(customerId)).get();
            //TODO security key and then get account from key? Check if you create request for your own account
            //TODO type/sanity check amount
            Payment request = new Payment(receiver, BigDecimal.valueOf(amount));
            request = paymentRepository.save(request);
            // TODO create URL to
            return request;
        } catch (NoSuchElementException e) {
            //TODO send 404 or similar
            return null;
        }
    }

    @RequestMapping(path = "payment/{paymentId}")
    public String getPayment(@PathVariable("paymentId") UUID paymentId, Model model) {
        Payment payment = (Payment) paymentRepository.findByPaymentId(paymentId);
        //TODO not found exception
        Customer user = (Customer) userService.getCurrentUser();
        model.addAttribute("user", user);
        model.addAttribute("payment", payment);
        return "payment";
    }

    @RequestMapping(path = "payment/{paymentId}/fulfill")
    @Transactional
    public String fulfillPayment(@PathVariable("paymentId") UUID paymentId, Model model,
                                 @ModelAttribute Payment payment,
                                 @ModelAttribute Customer user) {

        //TODO DRY! alles aus TransactionController
        BankAccount to = payment.getReceiver().getBankAccount();
        BankAccount from = user.getBankAccount();

        Transaction transaction = new Transaction(payment.getAmount(), payment.getDescription(), to, from);

        transactionService.executeTransaction(transaction);

        payment.setFulfilled(true);
        paymentRepository.save(payment);


        //TODO move into method or sth
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(user.getBankAccount(), user.getBankAccount());
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        return "overview";
    }

    //TODO API Endpoint to check if request is fulfilled
}
