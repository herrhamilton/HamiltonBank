package de.othr.sw.hamilton.service;

import de.majaf.voci.entity.Invitation;
import de.othr.sw.hamilton.entity.Advisor;
import de.othr.sw.hamilton.entity.Consulting;
import de.othr.sw.hamilton.repository.ConsultingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultingService {

    @Value("${appconfig.base-url}")
    private String baseUrl;

    @Value("${appconfig.voci.port}")
    private int vociPort;

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
        Consulting consulting = consultingRepository.findOneByCustomerAndIsResolvedFalse(userService.getCurrentCustomer());
        return consulting == null
                ? new Consulting()
                : consulting;
    }

    public Consulting acceptConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        Advisor advisor = (Advisor)userService.getCurrentUser();
        consulting.setAccepted(true);
        consulting.setAdvisor(advisor);

        //TODO move to config or sth
        String apiKey = advisor.getVociApiKey().toString();

        RequestEntity<Void> requestEntity = RequestEntity.post(baseUrl + ":" + vociPort + "/api/startCall")
                .header("securityToken", apiKey)
                .build();
        ResponseEntity<Invitation> responseEntity = restClient.exchange(requestEntity, Invitation.class);
        Invitation invitation = responseEntity.getBody();
        String accessToken = invitation.getAccessToken();
        consulting.setConsultingUrl(baseUrl + ":" + vociPort + "/invitation?=" + accessToken);
        //TODO weg mit dem Schmuh
        consulting.setAccessToken(accessToken);
        consulting.setAdvisorUrl(baseUrl + ":" + vociPort + "/call?=" + accessToken);
        consulting.setAcceptTime(new Date());
        consulting = consultingRepository.save(consulting);
        advisor.setRunningConsulting(consulting);
        //TODO userService oder Repo?
        userService.saveUser(advisor);
        return consulting;
    }

    public void closeConsulting(UUID consultingId) {
        Consulting consulting = consultingRepository.findOneByConsultingId(consultingId);
        //TODO UTC
        consulting.setEndTime(new Date());
        consulting.setResolved(true);
        //TODO Test jetz müssen alle Werte ausgefüllt sein?
        consulting = consultingRepository.save(consulting);
        Advisor advisor = consulting.getAdvisor();
        //TODO schöner?
        advisor.setRunningConsulting(null);
        userService.saveUser(advisor);

        String apiKey = "93164040-684b-43b9-b64f-5a0e6c5d4a12";
        //TODO wieso kann ich die Params end anhängen?
        String url = baseUrl + ":" + vociPort + "/endCall?accessToken=" + consulting.getAccessToken();
        RequestEntity<Void> requestEntity = RequestEntity.delete(
                url)
                .header("securityToken", apiKey)
                .build();
        restClient.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
    }
}
