package de.othr.sw.hamilton.service;

import de.majaf.voci.entity.Invitation;
import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.repository.ConsultingRepository;
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
public class ConsultingService {

    @Value("${appconfig.voci.url}")
    private String vociUrl;

    private final UserService userService;

    private final ConsultingRepository consultingRepository;

    private final RestTemplate restClient;

    public ConsultingService(UserService userService, ConsultingRepository consultingRepository, RestTemplate restClient) {
        this.userService = userService;
        this.consultingRepository = consultingRepository;
        this.restClient = restClient;
    }

    public Advisor createAdvisor(Advisor advisor) {
        //TODO give roles/authorities to customer/advisor?
        return (Advisor) userService.createUser(advisor);
    }

    public List<Consulting> getOpenRequests() {
        //TODO exception handling here necessary?
        return consultingRepository.findAllByIsResolvedFalse();
    }

    public Consulting getRequestForCurrentCustomer() {
        Consulting consulting = userService.getCurrentCustomer().getPendingConsulting();
        return consulting == null
                ? new Consulting()
                : consulting;
    }

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

    @Transactional
    public Consulting acceptConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        Advisor advisor = (Advisor)userService.getCurrentUser();
        consulting.setAccepted(true);
        consulting.setAdvisor(advisor);

        String apiKey = advisor.getVociApiKey().toString();

        Invitation invitation = startVociCall(apiKey);

        consulting.setAccessToken(invitation.getAccessToken());
        consulting.setAcceptTime(new Date());
        consulting = consultingRepository.save(consulting);
        advisor.setRunningConsulting(consulting);
        userService.saveUser(advisor);
        return consulting;
    }

    @Transactional
    public void closeConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        consulting.setEndTime(new Date());
        consulting.setResolved(true);
        //TODO Test jetz müssen alle Werte ausgefüllt sein?
        consulting = consultingRepository.save(consulting);
        Customer customer = consulting.getCustomer();
        Advisor advisor = consulting.getAdvisor();
        customer.setPendingConsulting(null);
        advisor.setRunningConsulting(null);
        userService.saveUser(advisor);
        userService.saveUser(customer);

        String apiKey = advisor.getVociApiKey().toString();
        closeVociCall(consulting.getAccessToken(), apiKey);
    }

    private Invitation startVociCall(String apiKey) {
        RequestEntity<Void> requestEntity = RequestEntity.post(vociUrl + "/api/startCall")
                .header("securityToken", apiKey)
                .build();
        ResponseEntity<Invitation> responseEntity = restClient.exchange(requestEntity, Invitation.class);
        return responseEntity.getBody();
    }

    private void closeVociCall(String accessToken, String apiKey) {
        //TODO wieso kann ich die Params end anhängen?
        String url = vociUrl + "/endCall?accessToken=" + accessToken;
        RequestEntity<Void> requestEntity = RequestEntity.delete(url)
                .header("securityToken", apiKey)
                .build();
        restClient.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
    }
}
