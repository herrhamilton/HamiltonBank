package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Long> {
    //TODO schöner machen oof
    List<Transaction> findByFromAccountOrToAccount(BankAccount a1, BankAccount a2);
}
