package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface IUserRepository extends CrudRepository<User, Long> {
    User findOneByUsername(String username);
}
