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

import java.io.Serializable;

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

        if(userRepository.findOneByUsername(user.getUsername()) != null) {
            // TODO Meldung dass User schon existiert
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        user = userRepository.save(user);
        //TODO input validation wo?

        if(user instanceof Customer) {
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
        return userRepository.findOneByUsername(username);
    }

    public User getCurrentUser() {
        //TODO sicherstellen dass jemand eingeloggt ist?
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return loadUserByUsername(username);
    }

    public Customer getCurrentCustomer() {
        //TODO kann man Customer iwie lokal halten oder muss man den jedesmal abfragen?
        return (Customer) getCurrentUser();
    }

    public Customer updateCustomer(Customer updated) {
        Customer customer = (Customer) loadUserByUsername(updated.getUsername());
        //TODO unit test : password (noch) und username, bankAccount, id NICHT ändern
        // TODO zu db query machen?
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
