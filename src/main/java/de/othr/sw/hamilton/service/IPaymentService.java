package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Payment;

import java.util.UUID;

public interface IPaymentService {
    Payment createPayment(Payment requested);

    Payment findPayment(UUID paymentId);

    void fulfillPayment(Payment payment);
}
