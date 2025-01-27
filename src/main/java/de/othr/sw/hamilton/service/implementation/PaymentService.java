package de.othr.sw.hamilton.service.implementation;

import de.othr.sw.hamilton.entity.*;
import de.othr.sw.hamilton.repository.IPaymentRepository;
import de.othr.sw.hamilton.service.IPaymentService;
import de.othr.sw.hamilton.service.ITransactionService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class PaymentService implements IPaymentService {

    @Value("${appconfig.base-url}")
    private String baseUrl;

    @Value("${server.port}")
    private int port;

    private final IUserService userService;

    private final IPaymentRepository paymentRepository;

    private final ITransactionService transactionService;

    public PaymentService(IUserService userService, IPaymentRepository paymentRepository, ITransactionService transactionService) {
        this.userService = userService;
        this.paymentRepository = paymentRepository;
        this.transactionService = transactionService;
    }

    @Override
    public Payment createPayment(PaymentRequest requested) {
        Payment payment = new Payment(requested);
        String paymentUrl = baseUrl + ":" + port + "/payment/" + payment.getPaymentId();
        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);
        return payment;
    }

    @Override
    @Transactional
    public void fulfillPayment(Payment payment) {

        Customer sender = userService.getCurrentCustomer();
        String receiverName = payment.getReceiverName();
        Customer receiver = (Customer) userService.loadUserByUsername(receiverName);
        BankAccount to = receiver.getBankAccount();
        BankAccount from = sender.getBankAccount();

        Transaction transaction = new Transaction(payment.getAmount(), payment.getDescription(), to, from);
        String senderName = transaction.getFromAccount().getOwner().getUsername();
        transactionService.executeTransaction(transaction);

        payment.fulfill(senderName);
        paymentRepository.save(payment);
    }

    @Override
    public Payment findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findOneByPaymentId(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment does not exist");
        }
        return payment;
    }
}
