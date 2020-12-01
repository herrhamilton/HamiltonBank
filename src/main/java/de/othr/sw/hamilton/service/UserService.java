package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public Customer createUser(Customer customer) {
        customer = userRepository.save(customer);
        //TODO input validation wo?
        //TODO zwischen Customer und Advisor unterscheiden
        BankAccount bankAccount = new BankAccount();
        bankAccount.setOwner(customer);
        bankAccount = bankAccountRepository.save(bankAccount);

        customer.setBankAccount(bankAccount);
        return userRepository.save(customer);
    }
}
