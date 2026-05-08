package com.example.mtg.market.service;

import com.example.mtg.market.dto.ArticleToBuy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartSyncService {

    private final CardmarketApiService cardmarketApiService;

    public CartSyncService(CardmarketApiService cardmarketApiService) {
        this.cardmarketApiService = cardmarketApiService;
    }

    public boolean syncCart(List<ArticleToBuy> articles) {
        try {
            return cardmarketApiService.addArticlesToCart(articles);
        } catch (Exception e) {
            // Log specific errors, e.g., if an article was bought by someone else
            System.err.println("Failed to sync cart: " + e.getMessage());
            return false;
        }
    }
}
