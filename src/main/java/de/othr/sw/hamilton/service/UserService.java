package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class UserService implements Serializable, UserDetailsService {

    private final UserRepository userRepository;

    private final BankAccountRepository bankAccountRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, BankAccountRepository bankAccountRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Customer createUser(Customer customer) {
        //TODO unique User
        customer.setPasswordHash(passwordEncoder.encode(customer.getPassword()));
        customer = userRepository.save(customer);
        //TODO input validation wo?
        BankAccount bankAccount = new BankAccount();
        bankAccount.setOwner(customer);
        bankAccount = bankAccountRepository.save(bankAccount);

        customer.setBankAccount(bankAccount);
        return userRepository.save(customer);
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findOneByUsername(username);
    }

    public Customer getCurrentCustomer() {
        //TODO sicherstellen dass jemand eingeloggt ist?
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return (Customer) loadUserByUsername(username);
    }

    public Customer updateCustomer(Customer updated) {
        //TODO fix des ganze Chustomer/User rumgeschmuh
        Customer customer = (Customer) loadUserByUsername(updated.getUsername());
        //TODO unit test : password (noch) und username, bankAccount, id NICHT ändern
        // TODO gehts auch iwie schöner?
        customer.setFirstName(updated.getFirstName());
        customer.setLastName(updated.getLastName());
        customer.setStonksApiKey(updated.getStonksApiKey());
        customer.setAddress(updated.getAddress());

        return userRepository.save(customer);
    }
}
