package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Customer;
import dev.wobu.stonks.entity.Portfolio;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class DepotService {
    @Value("${appconfig.stonks.url}")
    private String stonksUrl;

    private final RestTemplate restClient;

    private final UserService userService;

    public DepotService(RestTemplate restClient, UserService userService) {
        this.restClient = restClient;
        this.userService = userService;
    }

    public Portfolio getPortfolio() {
        Customer customer =  userService.getCurrentCustomer();
        UUID apiKey = customer.getStonksApiKey();
        //TODO schöner
        if(apiKey == null) {
            return null;
        } else {
            return getStonksPortfolio(apiKey.toString());
        }
    }

    private Portfolio getStonksPortfolio(String apiKey) {
        return null;
        /*
        RequestEntity<Void> requestEntity = RequestEntity.get(stonksUrl + "/api/v1/portfolio")
                .header("X-API-Key", apiKey)
                .build();
        //TODO handle 403 Forbidden
        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        return responseEntity.getBody();
    */
    }

    /*
    TODO remove bzw Link zu Stonks?
    public TaxReport getTaxReport(int year) {

        Customer customer =  userService.getCurrentCustomer();
        UUID apiKey = customer.getStonksApiKey();

        URI uri = UriComponentsBuilder.fromUriString(baseUrl + ":" + stonksPort +"/api/v1/taxreport/{year}").build(year);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("X-API-Key", apiKey.toString())
                .build();

        ResponseEntity<TaxReport> responseEntity = restClient.exchange(requestEntity, TaxReport.class);
        return responseEntity.getBody();
    }

    public TaxReport getLastYearsTaxReport() {
        return getTaxReport(Year.now().getValue() - 1);
    }
         */
}
