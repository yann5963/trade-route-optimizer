package com.example.mtg.market.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient cardmarketRestClient(CardmarketOAuthInterceptor interceptor, CardmarketProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestInterceptor(interceptor)
                .build();
    }
}
