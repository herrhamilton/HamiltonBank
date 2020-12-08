package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.BankAccount;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;
import de.othr.sw.hamilton.repository.BankAccountRepository;
import de.othr.sw.hamilton.repository.UserRepository;
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
