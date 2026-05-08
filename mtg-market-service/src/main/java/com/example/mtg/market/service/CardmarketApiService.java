package com.example.mtg.market.service;

import com.example.mtg.market.dto.CardmarketArticle;
import com.example.mtg.market.dto.CardmarketArticleResponse;
import com.example.mtg.market.dto.CardmarketProduct;
import com.example.mtg.market.dto.CardmarketProductResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Collections;
import java.util.List;

/**
 * Service to interact with the Cardmarket API.
 */
@Service
public class CardmarketApiService {

    private final RestClient cardmarketRestClient;

    public CardmarketApiService(RestClient cardmarketRestClient) {
        this.cardmarketRestClient = cardmarketRestClient;
    }

    /**
     * Finds products by name using the Cardmarket API.
     *
     * @param name The name of the product to search for.
     * @return A list of matching CardmarketProduct objects.
     */
    public List<CardmarketProduct> findProductsByName(String name) {
        try {
            CardmarketProductResponse response = cardmarketRestClient.get()
                    .uri("/ws/v2.0/products/find?search={name}", name)
                    .retrieve()
                    .body(CardmarketProductResponse.class);

            if (response != null && response.getProduct() != null) {
                return response.getProduct();
            }
        } catch (Exception e) {
            System.err.println("Error fetching products: " + e.getMessage());
        }
        return Collections.emptyList();
    }

    /**
     * Gets articles for a specific product ID.
     *
     * @param productId The ID of the product.
     * @return A list of articles for the given product.
     */
    public List<CardmarketArticle> getArticlesForProduct(Long productId) {
        try {
            CardmarketArticleResponse response = cardmarketRestClient.get()
                    .uri("/ws/v2.0/articles/{productId}", productId)
                    .retrieve()
                    .body(CardmarketArticleResponse.class);

            if (response != null && response.getArticle() != null) {
                return response.getArticle();
            }
        } catch (Exception e) {
            System.err.println("Error fetching articles for product " + productId + ": " + e.getMessage());
        }
        return Collections.emptyList();
    }
}
