package com.example.mtg.market.service;

import com.example.mtg.market.dto.CardmarketArticle;
import com.example.mtg.market.dto.CardmarketProduct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service to optimize the shopping cart.
 */
@Service
public class CartOptimizerService {

    private final CardmarketApiService cardmarketApiService;

    public CartOptimizerService(CardmarketApiService cardmarketApiService) {
        this.cardmarketApiService = cardmarketApiService;
    }

    /**
     * Optimizes the shopping cart by grouping articles by seller and calculating costs.
     *
     * @param cardNames The list of card names to find deals for.
     * @return A list of optimized cart data, including seller name, item count, and costs.
     */
    public List<Map<String, Object>> optimizeCart(List<String> cardNames) {
        // Map of Seller Username -> List of Articles available from them
        Map<String, List<CardmarketArticle>> sellerInventory = new HashMap<>();

        for (String cardName : cardNames) {
            List<CardmarketProduct> products = cardmarketApiService.findProductsByName(cardName);
            if (products.isEmpty()) continue;

            // Assume we want the first product match for simplicity
            Long productId = products.get(0).getIdProduct();
            List<CardmarketArticle> articles = cardmarketApiService.getArticlesForProduct(productId);

            for (CardmarketArticle article : articles) {
                // Filter by French sellers as requested
                if (article.getSeller() != null && "FR".equalsIgnoreCase(article.getSeller().getCountry())) {
                    String sellerName = article.getSeller().getUsername();

                    // We only want 1 of each card from a seller for simplicity in this mockup
                    List<CardmarketArticle> sellerArticles = sellerInventory.computeIfAbsent(sellerName, k -> new ArrayList<>());

                    boolean alreadyHasCard = sellerArticles.stream().anyMatch(a -> a.getIdProduct().equals(productId));
                    if (!alreadyHasCard) {
                        sellerArticles.add(article);
                    }
                }
            }
        }

        List<Map<String, Object>> sellerCarts = new ArrayList<>();

        for (Map.Entry<String, List<CardmarketArticle>> entry : sellerInventory.entrySet()) {
            String sellerName = entry.getKey();
            List<CardmarketArticle> articles = entry.getValue();

            BigDecimal itemsTotal = articles.stream()
                    .map(CardmarketArticle::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal shippingCost = BigDecimal.valueOf(1.50);
            if (itemsTotal.compareTo(BigDecimal.valueOf(25.00)) > 0) {
                shippingCost = BigDecimal.valueOf(5.00); // Tracked shipping required over 25 EUR
            }

            BigDecimal totalCost = itemsTotal.add(shippingCost);

            Map<String, Object> cartInfo = new HashMap<>();
            cartInfo.put("sellerName", sellerName);
            cartInfo.put("itemCount", articles.size());
            cartInfo.put("itemsTotal", itemsTotal);
            cartInfo.put("shippingCost", shippingCost);
            cartInfo.put("totalCost", totalCost);
            cartInfo.put("articles", articles);

            sellerCarts.add(cartInfo);
        }

        // Sort by total cost ascending
        sellerCarts.sort(Comparator.comparing(m -> (BigDecimal) m.get("totalCost")));

        // Return top 3 options
        return sellerCarts.stream().limit(3).collect(Collectors.toList());
    }
}
