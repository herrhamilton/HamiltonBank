package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Payment;
import de.othr.sw.hamilton.entity.PaymentRequest;

import java.util.UUID;

public interface IPaymentService {
    Payment createPayment(PaymentRequest requested);

    Payment findPayment(UUID paymentId);

    void fulfillPayment(Payment payment);
}
