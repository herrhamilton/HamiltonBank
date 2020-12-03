package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class TransactionService implements Serializable {

    @Autowired
    private TransactionRepository transactionRepository;

    //TODO Empty constructor needed when there is no other constructor?
    public TransactionService() {}

    public boolean persistTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
        return true;
    }
}
