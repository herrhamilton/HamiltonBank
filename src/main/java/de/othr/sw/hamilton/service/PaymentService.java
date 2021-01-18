package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.PaymentRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    private final UserRepository userRepository;

    private final PaymentRepository paymentRepository;

    private final TransactionService transactionService;

    public PaymentService(UserRepository userRepository, PaymentRepository paymentRepository, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.transactionService = transactionService;
    }

    public Payment createPayment(String username, int amount) {
        //TODO error handling bei falschem username/amount
        //TODO add description to payment - move payment creation from path into body?
        Customer receiver = (Customer) userRepository.findOneByUsername(username);
        //TODO type/sanity check amount of payments, transactions,...
        Payment payment = new Payment(receiver.getUsername(), BigDecimal.valueOf(amount));
        payment = paymentRepository.save(payment);
        return payment;
    }

    @Transactional
    public void fulfillPayment(Payment payment, Customer user) {

        try {
            String receiverName = payment.getReceiverName();
            Customer receiver = (Customer) userRepository.findOneByUsername(receiverName);
            BankAccount to = receiver.getBankAccount();
            BankAccount from = user.getBankAccount();

            Transaction transaction = new Transaction(payment.getAmount(), payment.getDescription(), to, from);
            transactionService.executeTransaction(transaction);

            payment.setSenderName(user.getUsername());
            payment.setFulfilled(true);
            paymentRepository.save(payment);
        } catch (Exception e) {
            //TODO receiver not found
        }
    }

    public Payment findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findOneByPaymentId(paymentId);
        return payment;
    }
}
