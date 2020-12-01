package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.BankAccount;
import org.springframework.data.repository.CrudRepository;

public interface BankAccountRepository extends CrudRepository<BankAccount, Long> {
}
