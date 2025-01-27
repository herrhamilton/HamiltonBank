package de.othr.sw.hamilton.repository;

import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface IConsultingRepository extends CrudRepository<Consulting, Long> {
    List<Consulting> findAllByIsOpenTrue();

    Consulting findOneByConsultingId(UUID consultingId);
}
