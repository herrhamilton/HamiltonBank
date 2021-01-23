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
        BankAccount to = receiver.getBankAccount();
        BankAccount from = userService.getCurrentCustomer().getBankAccount();

        Transaction transaction = new Transaction(transactionForm.getAmount(), transactionForm.getDescription(), to, from);
        // TODO Input Verification, negative Werte einzahlen
        executeTransaction(transaction);
    }

    public void depositMoney(int amount) {
        Customer customer = userService.getCurrentCustomer();
        //TODO Komponentendiagramm ändern wenn Message Queuing zum Bezahlen benutzt wird (hat nix mit deposit zu tun^^)
        Transaction t = new Transaction(BigDecimal.valueOf(amount), "Einzahlung", customer.getBankAccount());

        executeTransaction(t);
    }

    public String generateStatementCsv() {
        //TODO change BankAccount to Customer
        List<Transaction> transactions = findTransactionsForBankAccount(userService.getCurrentCustomer().getBankAccount());
        String csvData = "";
        try(CharArrayWriter writer = new CharArrayWriter()) {

            StatefulBeanToCsv beanToCsv = new StatefulBeanToCsvBuilder(writer).build();
            beanToCsv.write(transactions);
            csvData = writer.toString();
        } catch (CsvRequiredFieldEmptyException e) {
            //TODO
            e.printStackTrace();
        } catch (CsvDataTypeMismatchException e) {
            e.printStackTrace();
        }
        return csvData;
    }

}
