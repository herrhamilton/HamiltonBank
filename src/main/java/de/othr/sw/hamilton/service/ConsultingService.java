package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.repository.ConsultingRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultingService {

    private final UserService userService;

    private final ConsultingRepository consultingRepository;

    public ConsultingService(UserService userService, ConsultingRepository consultingRepository) {
        this.userService = userService;
        this.consultingRepository = consultingRepository;
    }

    public Consulting createConsulting(Consulting consulting) {
        consulting.setRequestTime(new Date());
        consulting.setCustomer(userService.getCurrentCustomer());

        consulting = consultingRepository.save(consulting);
        return consulting;
    }

    public List<Consulting> getOpenRequests() {
        //TODO exception handling here necessary?
        return consultingRepository.findAllByIsResolvedFalse();
    }

    public Advisor createAdvisor(Advisor advisor) {
        //TODO give roles/authorities to customer/advisor?
        return (Advisor) userService.createUser(advisor);
    }

    public Consulting getRequestForCurrentCustomer() {
        Consulting consulting = consultingRepository.findOneByCustomer(userService.getCurrentCustomer());
        return consulting == null
                ? new Consulting()
                : consulting;
    }

    public void acceptConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        consulting.setAccepted(true);
        consulting.setAdvisor((Advisor)userService.getCurrentUser());
        //set Voci call Url
        //new window with voci call
    }

    public String joinConsulting(UUID consultingId) {
        //new voci window mit consulting call
        return "consulting";
    }
}
