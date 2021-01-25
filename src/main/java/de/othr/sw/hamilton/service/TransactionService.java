package de.othr.sw.hamilton.service;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.TransactionRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionService implements Serializable {

    private final UserService userService;

    private final UserRepository userRepository;

    private final TransactionRepository transactionRepository;

    private final BankAccountRepository bankAccountRepository;

    public TransactionService(UserService userService, UserRepository userRepository, TransactionRepository transactionRepository, BankAccountRepository bankAccountRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Transactional
    public void executeTransaction(Transaction transaction) {
        if(transaction.getFromAccount() != null) {
            BankAccount fromAccount = transaction.getFromAccount();
            fromAccount.withdraw(transaction.getAmount());
            bankAccountRepository.save(fromAccount);
        }
        BankAccount toAccount = transaction.getToAccount();
        toAccount.deposit(transaction.getAmount());
        bankAccountRepository.save(toAccount);
        transactionRepository.save(transaction);
    }


    public List<Transaction> findTransactionsForBankAccount(BankAccount bankAccount) {
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(bankAccount, bankAccount);
        transactions.sort(Collections.reverseOrder());
        return transactions;
    }

    public void sendTransaction(TransactionForm transactionForm) {
        Customer receiver = (Customer) userRepository.findOneByUsername(transactionForm.getToUsername());
        //TODO input validation
        BankAccount to = receiver.getBankAccount();
        BankAccount from = userService.getCurrentCustomer().getBankAccount();

        BigDecimal amount = getAmountFromString(transactionForm.getAmountString());
        Transaction transaction = new Transaction(amount, transactionForm.getDescription(), to, from);
        executeTransaction(transaction);
    }

    public void depositMoney(BigDecimal amount) {
        Customer customer = userService.getCurrentCustomer();
        Transaction t = new Transaction(amount, "Einzahlung", customer.getBankAccount());

        executeTransaction(t);
    }

    public String generateStatementCsv() {
        List<Transaction> transactions = findTransactionsForBankAccount(userService.getCurrentCustomer().getBankAccount());
        if(transactions.size() == 0) {
            return null;
        }
        String csvData = "";
        try(CharArrayWriter writer = new CharArrayWriter()) {

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
            beanToCsv.write(transactions);
            csvData = writer.toString();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            e.printStackTrace();
            return null;
        }
        return csvData;
    }

    public BigDecimal getAmountFromString(String amountString) {
        // TODO ggf Unit Testing
        amountString = amountString.replace(",", ".");
        BigDecimal amount = new BigDecimal(amountString);
        return amount;
    }
}
