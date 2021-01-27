package de.othr.sw.hamilton.service.implementation;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.Transaction;
import de.othr.sw.hamilton.entity.TransactionForm;
import de.othr.sw.hamilton.repository.IBankAccountRepository;
import de.othr.sw.hamilton.repository.ITransactionRepository;
import de.othr.sw.hamilton.repository.IUserRepository;
import de.othr.sw.hamilton.service.ITransactionService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class TransactionService implements Serializable, ITransactionService {

    private final IUserService userService;

    private final IUserRepository userRepository;

    private final ITransactionRepository transactionRepository;

    private final IBankAccountRepository bankAccountRepository;

    public TransactionService(IUserService userService, IUserRepository userRepository, ITransactionRepository transactionRepository, IBankAccountRepository bankAccountRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
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


    @Override
    public List<Transaction> findTransactionsForBankAccount(BankAccount bankAccount) {
        List<Transaction> transactions = transactionRepository.findByFromAccountOrToAccount(bankAccount, bankAccount);
        transactions.sort(Collections.reverseOrder());
        return transactions;
    }

    @Override
    public void sendTransaction(TransactionForm transactionForm) {
        Customer receiver = (Customer) userRepository.findOneByUsername(transactionForm.getToUsername());
        //TODO input validation
        BankAccount to = receiver.getBankAccount();
        BankAccount from = userService.getCurrentCustomer().getBankAccount();

        BigDecimal amount = getAmountFromString(transactionForm.getAmount());
        Transaction transaction = new Transaction(amount, transactionForm.getDescription(), to, from);
        executeTransaction(transaction);
    }

    @Override
    public void depositMoney(BigDecimal amount) {
        Customer customer = userService.getCurrentCustomer();
        Transaction t = new Transaction(amount, "Einzahlung", customer.getBankAccount());

        executeTransaction(t);
    }

    @Override
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

    @Override
    public BigDecimal getAmountFromString(String amountString) {
        // TODO ggf Unit Testing
        amountString = amountString.replace(",", ".");
        BigDecimal amount = new BigDecimal(amountString);
        return amount;
    }
}
