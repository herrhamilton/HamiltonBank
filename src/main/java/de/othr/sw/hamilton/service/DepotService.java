package de.othr.sw.hamilton.service;

import de.othr.sw.hamilton.entity.Customer;
import dev.wobu.stonks.entity.Portfolio;
import dev.wobu.stonks.entity.TaxReport;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
public class DepotService {

    private final RestTemplate restClient;

    private final UserService userService;

    public DepotService(RestTemplate restClient, UserService userService) {
        this.restClient = restClient;
        this.userService = userService;
    }

    //TODO update to WebClient?
    public Portfolio getPortfolio() {
        //TODO getCurrentUser maybe zu Customer?
        Customer customer = (Customer) userService.getCurrentUser();
        String apiKey = customer.getStonksApiKey();
        //TODO alle URLs in application.properties auslagern
        RequestEntity<Void> requestEntity = RequestEntity.get("http://im-codd.oth-regensburg.de:8933/api/v1/portfolio")
                .header("X-API-Key", apiKey)
                .build();

        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        getTaxReport(2020);
        return responseEntity.getBody();
    }

    public TaxReport getTaxReport(int year) {

        //TODO DRY mit oben
        Customer customer = (Customer) userService.getCurrentUser();
        String apiKey = customer.getStonksApiKey();

        Map<String, String> params = new HashMap<>();
        //TODO alle URLs in application.properties auslagern
        //TODO exception year parse?
        URI uri = UriComponentsBuilder.fromUriString("http://im-codd.oth-regensburg.de:8933/api/v1/taxreport/{year}").build(year);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("X-API-Key", apiKey)
                .build();

        ResponseEntity<TaxReport> responseEntity = restClient.exchange(requestEntity, TaxReport.class);
        return responseEntity.getBody();
    }

    public TaxReport getLastYearsTaxReport() {
        return getTaxReport(Year.now().getValue() - 1);
    }
}
