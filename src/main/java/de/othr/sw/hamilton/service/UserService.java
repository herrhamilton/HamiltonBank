package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.openmbean.KeyAlreadyExistsException;
import java.io.Serializable;
import java.util.NoSuchElementException;

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

    public User createUser(User user) {

        if (userRepository.findOneByUsername(user.getUsername()) != null) {
            throw new KeyAlreadyExistsException("Username already in use.");
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user = userRepository.save(user);

        if (user instanceof Customer) {
            Customer customer = (Customer) user;
            BankAccount bankAccount = new BankAccount();
            bankAccount.setOwner(customer);
            bankAccount = bankAccountRepository.save(bankAccount);

            customer.setBankAccount(bankAccount);
            return userRepository.save(customer);
        }

        return user;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        User user =  userRepository.findOneByUsername(username);
        if(user == null) {
            //TODO catch this in AuthStuff?
            throw new UsernameNotFoundException("User mit Username " + username + " existiert nicht.");
        }
        return user;
    }

    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return loadUserByUsername(username);
    }

    public Customer getCurrentCustomer() {
        //TODO kann man Customer iwie lokal halten oder muss man den jedesmal abfragen?
        return (Customer) getCurrentUser();
    }

    public Customer updateCustomer(Customer updated) {
        Customer customer = getCurrentCustomer();
        customer.setFirstName(updated.getFirstName());
        customer.setLastName(updated.getLastName());
        customer.setStonksApiKey(updated.getStonksApiKey());
        customer.setAddress(updated.getAddress());

        return userRepository.save(customer);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
