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
public class PortfolioService {
    @Value("${appconfig.stonks.url}")
    private String stonksUrl;

    private final RestTemplate restClient;

    private final UserService userService;

    public PortfolioService(RestTemplate restClient, UserService userService) {
        this.restClient = restClient;
        this.userService = userService;
    }

    public Portfolio getStonksPortfolio() {
        Customer customer = userService.getCurrentCustomer();
        UUID apiKey = customer.getStonksApiKey();
        if (apiKey == null) {
            return null;
        }

        RequestEntity<Void> requestEntity = RequestEntity.get(stonksUrl + "/api/v1/portfolio")
                .header("X-API-Key", apiKey.toString())
                .build();
        //TODO handle 403 Forbidden
        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        return responseEntity.getBody();
    }
    //TODO link zu stonks?
}
