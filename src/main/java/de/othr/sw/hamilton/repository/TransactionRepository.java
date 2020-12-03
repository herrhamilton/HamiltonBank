package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
}
