package de.othr.sw.hamilton.service.implementation;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.IPaymentRepository;
import de.othr.sw.hamilton.repository.IUserRepository;
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

    private final IUserRepository userRepository;

    private final IPaymentRepository paymentRepository;

    private final ITransactionService transactionService;

    public PaymentService(IUserService userService, IUserRepository userRepository, IPaymentRepository paymentRepository, ITransactionService transactionService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
        this.transactionService = transactionService;
    }

    @Override
    public Payment createPayment(Payment requested) {
        Payment payment = new Payment(requested.getReceiverName(), requested.getAmount(), requested.getDescription());
        String paymentUrl = baseUrl + ":" + port + "/payment/" + payment.getPaymentId();
        payment.setPaymentUrl(paymentUrl);
        payment = paymentRepository.save(payment);
        return payment;
    }

    @Override
    @Transactional
    public void fulfillPayment(Payment payment) {

        try {
            Customer sender = userService.getCurrentCustomer();
            String receiverName = payment.getReceiverName();
            Customer receiver = (Customer) userRepository.findOneByUsername(receiverName);
            BankAccount to = receiver.getBankAccount();
            BankAccount from = sender.getBankAccount();

            Transaction transaction = new Transaction(payment.getAmount(), payment.getDescription(), to, from);
            transactionService.executeTransaction(transaction);

            payment.setSenderName(sender.getUsername());
            payment.setFulfilled(true);
            paymentRepository.save(payment);
        } catch (Exception e) {
            //TODO receiver not found
        }
    }

    @Override
    public Payment findPayment(UUID paymentId) {
        Payment payment = paymentRepository.findOneByPaymentId(paymentId);
        if(payment == null) {
            throw new IllegalArgumentException("Payment does not exist");
        }
        return payment;
    }
}
