package de.othr.sw.hamilton.service;

import ch.qos.logback.core.pattern.color.BoldCyanCompositeConverter;
import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
public class UserService implements Serializable, UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Customer createUser(Customer customer) {
        customer.setPasswordHash(passwordEncoder.encode(customer.getPassword()));
        customer = userRepository.save(customer);
        //TODO input validation wo?
        //TODO zwischen Customer und Advisor unterscheiden
        BankAccount bankAccount = new BankAccount();
        bankAccount.setOwner(customer);
        bankAccount = bankAccountRepository.save(bankAccount);

        customer.setBankAccount(bankAccount);
        return userRepository.save(customer);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).stream().findAny().orElseThrow();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(s).stream().findAny().orElseThrow(() -> new UsernameNotFoundException("User mit Email nicht gefunden: " + s));
        return user;
    }
}
