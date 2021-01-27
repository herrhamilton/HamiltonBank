package de.othr.sw.hamilton.service.implementation;

import de.othr.sw.hamilton.entity.Customer;
import de.othr.sw.hamilton.service.IPortfolioService;
import de.othr.sw.hamilton.service.IUserService;
import dev.wobu.stonks.entity.Portfolio;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
public class PortfolioService implements IPortfolioService {

    private final RestTemplate restClient;

    private final IUserService userService;

    public PortfolioService(@Qualifier("stonks") RestTemplate restClient, IUserService userService) {
        this.restClient = restClient;
        this.userService = userService;
    }

    @Override
    public Portfolio getStonksPortfolio() {
        Customer customer = userService.getCurrentCustomer();
        UUID apiKey = customer.getStonksApiKey();
        if (apiKey == null) {
            return null;
        }
        RequestEntity<Void> requestEntity = RequestEntity.get("/api/v1/portfolio")
                .header("X-API-Key", apiKey.toString())
                .build();
        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        return responseEntity.getBody();
    }
}
