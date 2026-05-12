package com.example.mtg.collection.service;

import com.example.mtg.collection.dto.DealResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public MarketClient(@Value("${app.market.api.url:http://localhost:8082}") String baseUrl) {
        this.restTemplate = new RestTemplate();
        this.baseUrl = baseUrl;
    }

    public List<DealResponseDTO> getDeals() {
        try {
            ResponseEntity<List<DealResponseDTO>> response = restTemplate.exchange(
                    baseUrl + "/api/deals",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DealResponseDTO>>() {}
            );
            return response.getBody() != null ? response.getBody() : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public void ignoreDeal(Long id) {
        try {
            restTemplate.put(baseUrl + "/api/deals/" + id + "/status?status=IGNORED", null);
        } catch (Exception e) {
            // Log error
        }
    }

    public void addDealToCart(Long id) {
        try {
            restTemplate.put(baseUrl + "/api/deals/" + id + "/status?status=CART", null);
        } catch (Exception e) {
            // Log error
        }
    }

    public Long getDealsCount() {
        try {
            return restTemplate.getForObject(baseUrl + "/api/deals/count", Long.class);
        } catch (Exception e) {
            return 0L;
        }
    }
}
