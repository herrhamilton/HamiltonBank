package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;

import java.math.BigDecimal;
import java.util.List;

public interface ITransactionService {
    void executeTransaction(Transaction transaction);

    List<Transaction> findTransactionsForBankAccount(BankAccount bankAccount);

    void sendTransaction(TransactionForm transactionForm);

    void depositMoney(BigDecimal amount);

    String generateStatementCsv();

    BigDecimal getAmountFromString(String amountString);
}
