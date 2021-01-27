package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.entity.User;

public interface IUserService {

    User createUser(User user);

    User loadUserByUsername(String username);

    User getCurrentUser();

    Customer getCurrentCustomer();

    Customer updateCustomer(Customer updated);

    User saveUser(User user);
}
