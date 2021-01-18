package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.Payment;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
    Payment findOneByPaymentId(UUID paymentId);
}
