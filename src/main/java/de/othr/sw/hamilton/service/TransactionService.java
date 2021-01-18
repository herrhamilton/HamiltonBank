package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.List;

@Service
public class TransactionService implements Serializable {

    private final TransactionRepository transactionRepository;

    private final BankAccountRepository bankAccountRepository;

    public TransactionService(TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public boolean executeTransaction(Transaction transaction) {
        if(transaction.getFromAccount() != null) {
            BankAccount fromAccount = transaction.getFromAccount();
            fromAccount.withdraw(transaction.getAmount());
            bankAccountRepository.save(fromAccount);
        }
        BankAccount toAccount = transaction.getToAccount();
        toAccount.deposit(transaction.getAmount());
        bankAccountRepository.save(toAccount);
        transactionRepository.save(transaction);
        return true;
    }


    public List<Transaction> findTransactionsForBankAccount(BankAccount bankAccount) {
        return transactionRepository.findByFromAccountOrToAccount(bankAccount, bankAccount);
    }
}
