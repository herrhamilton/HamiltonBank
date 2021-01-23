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

    @Value("${appconfig.base-url}")
    private String baseUrl;

    @Value("${appconfig.stonks.port}")
    private int stonksPort;

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
        }
        RequestEntity<Void> requestEntity = RequestEntity.get(baseUrl + ":" + stonksPort + "/api/v1/portfolio")
                .header("X-API-Key", apiKey.toString())
                .build();

        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        return responseEntity.getBody();
    }

    /*
    TODO remove?
    public TaxReport getTaxReport(int year) {

        //TODO DRY mit oben
        Customer customer =  userService.getCurrentCustomer();
        UUID apiKey = customer.getStonksApiKey();

        //TODO exception falsches year Format?
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
