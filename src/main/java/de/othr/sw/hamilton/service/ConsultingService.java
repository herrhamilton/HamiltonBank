package de.othr.sw.hamilton.service;

import de.majaf.voci.entity.Invitation;
import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.repository.ConsultingRepository;
import dev.wobu.stonks.entity.Portfolio;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultingService {

    private final UserService userService;

    private final ConsultingRepository consultingRepository;

    private final RestTemplate restClient;

    public ConsultingService(UserService userService, ConsultingRepository consultingRepository, RestTemplate restClient) {
        this.userService = userService;
        this.consultingRepository = consultingRepository;
        this.restClient = restClient;
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

    public Consulting acceptConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        Advisor advisor = (Advisor)userService.getCurrentUser();
        consulting.setAccepted(true);
        consulting.setAdvisor(advisor);
        //set Voci call Url
        //new window with voci call

        //TODO move to config or sth
        String apiKey = "6d48a1d5-1a67-40af-8da1-9c365247ea1f";
        Invitation inv = restClient.getForObject("http://im-codd.oth-regensburg.de:8945/api/startCall?securityToken=" + apiKey, Invitation.class);

        consulting.setConsultingUrl("http://im-codd.oth-regensburg.de:8945/invitation?=" + inv.getAccessToken());
        consulting = consultingRepository.save(consulting);
        advisor.setRunningConsulting(consulting);
        //TODO userService oder Repo?
        userService.saveUser(advisor);
        return consulting;
    }
}
