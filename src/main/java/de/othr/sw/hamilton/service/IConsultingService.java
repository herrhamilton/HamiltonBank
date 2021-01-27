package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;

import java.util.List;
import java.util.UUID;

public interface IConsultingService {
    Advisor createAdvisor(Advisor advisor);

    List<Consulting> getOpenConsultings();

    Consulting createConsulting(Consulting consulting);

    Consulting acceptConsulting(UUID consultingId);

    void closeConsulting(String summary, UUID consultingId);

    void cancelConsulting();
}
