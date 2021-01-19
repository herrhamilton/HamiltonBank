package de.othr.sw.hamilton.service;

import dev.wobu.stonks.entity.Portfolio;
import dev.wobu.stonks.entity.TaxReport;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
public class DepotService {

    private final RestTemplate restClient;

    public DepotService(RestTemplate restClient) {
        this.restClient = restClient;
    }
    //TODO update to WebClient?
    public Portfolio getPortfolio() {
        Map<String, String> params = new HashMap<>();
        //TODO alle URLs in application.properties auslagern
        //URI portfolioUrl = UriComponentsBuilder.fromUriString("http://im-codd.oth-regensburg.de:8933/api/v1/portfolio").build(params);

        RequestEntity<Void> requestEntity = RequestEntity.get("http://im-codd.oth-regensburg.de:8933/api/v1/portfolio")
                .header("X-API-Key", "fd16b8d3-fdcc-4c9a-908a-bd8d64ddfc0b")
                .build();

        ResponseEntity<Portfolio> responseEntity = restClient.exchange(requestEntity, Portfolio.class);
        getTaxReport(2020);
        return responseEntity.getBody();
    }

    public TaxReport getTaxReport(int year) {
        Map<String, String> params = new HashMap<>();
        //TODO alle URLs in application.properties auslagern
        //TODO exception year parse?
        URI uri = UriComponentsBuilder.fromUriString("http://im-codd.oth-regensburg.de:8933/api/v1/taxreport/{year}").build(year);

        RequestEntity<Void> requestEntity = RequestEntity.get(uri)
                .header("X-API-Key", "fd16b8d3-fdcc-4c9a-908a-bd8d64ddfc0b")
                .build();

        ResponseEntity<TaxReport> responseEntity = restClient.exchange(requestEntity, TaxReport.class);
        return responseEntity.getBody();
    }
}
