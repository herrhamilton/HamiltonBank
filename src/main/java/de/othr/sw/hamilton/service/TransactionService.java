package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class TransactionService implements Serializable {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public boolean persistTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        return true;
    }
}
