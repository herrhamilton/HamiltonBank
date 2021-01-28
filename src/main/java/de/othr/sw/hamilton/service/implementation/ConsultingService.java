package de.othr.sw.hamilton.service.implementation;

import de.majaf.voci.entity.Invitation;
import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.repository.IConsultingRepository;
import de.othr.sw.hamilton.service.IConsultingService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Scope(value = BeanDefinition.SCOPE_SINGLETON)
public class ConsultingService implements IConsultingService {

    private final IUserService userService;

    private final IConsultingRepository consultingRepository;

    private final RestTemplate restClient;

    public ConsultingService(IUserService userService, IConsultingRepository consultingRepository, @Qualifier("voci") RestTemplate restClient) {
        this.userService = userService;
        this.consultingRepository = consultingRepository;
        this.restClient = restClient;
    }

    @Override
    public Advisor createAdvisor(Advisor advisor) {
        return (Advisor) userService.createUser(advisor);
    }

    @Override
    public List<Consulting> getOpenConsultings() {
        return consultingRepository.findAllByIsOpenTrue();
    }

    @Override
    @Transactional
    public Consulting createConsulting(Consulting consulting) {
        Customer customer = userService.getCurrentCustomer();
        consulting.setRequestTime(new Date());
        consulting.setCustomer(customer);
        consulting = consultingRepository.save(consulting);

        return consulting;
    }

    @Override
    @Transactional
    public Consulting acceptConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        Advisor advisor = (Advisor) userService.getCurrentUser();

        String apiKey = advisor.getVociApiKey().toString();
        Invitation invitation = startVociCall(apiKey);

        consulting.setAccepted(true);
        consulting.setAdvisor(advisor);
        consulting.setAccessToken(invitation.getAccessToken());
        consulting.setAcceptTime(new Date());
        consulting = consultingRepository.save(consulting);
        return consulting;
    }

    @Override
    @Transactional
    public void closeConsulting(String summary, UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        consulting.setEndTime(new Date());
        consulting.setOpen(false);
        consulting.setSummary(summary);
        consulting = consultingRepository.save(consulting);

        Advisor advisor = consulting.getAdvisor();
        String apiKey = advisor.getVociApiKey().toString();
        closeVociCall(consulting.getAccessToken(), apiKey);
    }

    @Override
    @Transactional
    public void cancelConsulting() {
        Customer customer = userService.getCurrentCustomer();
        Consulting consulting = customer.getOpenConsulting();
        if (consulting != null) {
            consulting.setOpen(false);
            consulting.setEndTime(new Date());
            consultingRepository.save(consulting);
        }
    }

    private Invitation startVociCall(String apiKey) {
        RequestEntity<Void> requestEntity = RequestEntity
                .post("/api/startCall")
                .header("securityToken", apiKey)
                .build();
        ResponseEntity<Invitation> responseEntity = restClient.exchange(requestEntity, Invitation.class);
        return responseEntity.getBody();
    }

    private void closeVociCall(String accessToken, String apiKey) {
        try {
            String url = "/endCall?accessToken=" + accessToken;
            RequestEntity<Void> requestEntity = RequestEntity.delete(url)
                    .header("securityToken", apiKey)
                    .build();
            restClient.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
