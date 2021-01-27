package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.BankAccount;
import org.springframework.data.repository.CrudRepository;

public interface IBankAccountRepository extends CrudRepository<BankAccount, Long> {
}
