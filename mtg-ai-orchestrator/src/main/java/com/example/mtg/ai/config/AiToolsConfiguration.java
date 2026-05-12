package com.example.mtg.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.web.client.RestClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Configuration
public class AiToolsConfiguration {

    @Value("${app.market.api.url:http://localhost:8082}")
    private String marketApiUrl;

    public record CardSearchRequest(List<String> cardNames) {}

    // We expect a list of maps from the optimizer service
    public record CardSearchResponse(List<Map<String, Object>> suggestedCarts) {}

    @Bean
    @Description("Trouve les meilleures combinaisons d'achats de cartes Magic en optimisant les frais de port")
    public Function<CardSearchRequest, CardSearchResponse> searchBestOffers() {
        return request -> {
            try {
                RestClient restClient = RestClient.create();

                System.out.println("AI requested optimization for: " + request.cardNames());

                List<Map<String, Object>> response = restClient.post()
                        .uri(marketApiUrl + "/api/market/optimize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request.cardNames())
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

                return new CardSearchResponse(response);
            } catch (Exception e) {
                System.err.println("Error calling optimizer: " + e.getMessage());
                return new CardSearchResponse(List.of());
            }
        };
    }
}
