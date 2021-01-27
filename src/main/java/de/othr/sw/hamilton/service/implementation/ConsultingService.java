package de.othr.sw.hamilton.service.implementation;

import de.majaf.voci.entity.Invitation;
import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.repository.IConsultingRepository;
import de.othr.sw.hamilton.service.IConsultingService;
import de.othr.sw.hamilton.service.IUserService;
import org.springframework.beans.factory.annotation.Value;
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
public class ConsultingService implements IConsultingService {

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    private final IUserService userService;

    private final IConsultingRepository consultingRepository;

    private final RestTemplate restClient;

    public ConsultingService(IUserService userService, IConsultingRepository consultingRepository, RestTemplate restClient) {
        this.userService = userService;
        this.consultingRepository = consultingRepository;
        this.restClient = restClient;
    }

    @Override
    public Advisor createAdvisor(Advisor advisor) {
        //TODO give roles/authorities to customer/advisor?
        return (Advisor) userService.createUser(advisor);
    }

    @Override
    public List<Consulting> getOpenConsultings() {
        return consultingRepository.findAllByIsResolvedFalseAndIsCancelledFalse();
    }

    @Override
    @Transactional
    public Consulting createConsulting(Consulting consulting) {
        Customer customer = userService.getCurrentCustomer();
        consulting.setRequestTime(new Date());
        consulting.setCustomer(customer);
        consulting = consultingRepository.save(consulting);

        customer.setPendingConsulting(consulting);
        userService.saveUser(customer);

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
        advisor.setRunningConsulting(consulting);
        userService.saveUser(advisor);
        return consulting;
    }

    @Override
    @Transactional
    public void closeConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        consulting.setEndTime(new Date());
        consulting.setResolved(true);
        consulting = consultingRepository.save(consulting);

        Customer customer = consulting.getCustomer();
        customer.setPendingConsulting(null);

        Advisor advisor = consulting.getAdvisor();
        advisor.setRunningConsulting(null);

        userService.saveUser(customer);
        userService.saveUser(advisor);

        String apiKey = advisor.getVociApiKey().toString();
        closeVociCall(consulting.getAccessToken(), apiKey);
    }

    @Override
    @Transactional
    public void cancelConsulting() {
        Customer customer = userService.getCurrentCustomer();
        Consulting consulting = customer.getPendingConsulting();
        if (consulting != null) {
            customer.setPendingConsulting(null);
            consulting.setCancelled(true);
            consulting.setEndTime(new Date());
            consultingRepository.save(consulting);
            userService.saveUser(customer);
        }
    }

    private Invitation startVociCall(String apiKey) {
        RequestEntity<Void> requestEntity = RequestEntity.post(vociUrl + "/api/startCall")
                .header("securityToken", apiKey)
                .build();
        ResponseEntity<Invitation> responseEntity = restClient.exchange(requestEntity, Invitation.class);
        return responseEntity.getBody();
    }

    private void closeVociCall(String accessToken, String apiKey) {
        try {
            //TODO wieso kann ich die Params end anhängen?
            String url = vociUrl + "/endCall?accessToken=" + accessToken;
            RequestEntity<Void> requestEntity = RequestEntity.delete(url)
                    .header("securityToken", apiKey)
                    .build();
            restClient.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);

        } catch (Exception e) {
            //TODO logging voci cannot be closed?
        }
    }
}
