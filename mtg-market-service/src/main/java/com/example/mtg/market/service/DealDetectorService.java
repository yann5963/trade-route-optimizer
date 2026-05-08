package com.example.mtg.market.service;

import com.example.mtg.market.entity.CurrentDeal;
import com.example.mtg.market.repository.CurrentDealRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.List;

/**
 * Service responsible for periodically scanning and detecting new market deals.
 * <p>
 * This service currently uses a mock implementation to simulate the discovery
 * of deals based on card prices and trends.
 */
@Service
public class DealDetectorService {

    private final CurrentDealRepository currentDealRepository;
    private final JdbcTemplate jdbcTemplate;
    private final Random random = new Random();

    public DealDetectorService(CurrentDealRepository currentDealRepository, JdbcTemplate jdbcTemplate) {
        this.currentDealRepository = currentDealRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Scheduled task that runs periodically to find new deals.
     * <p>
     * It randomly selects a known card ID and generates a mocked deal
     * if the randomized price meets the criteria (e.g., &gt; 15% savings).
     * The deal is then saved to the database.
     */
    @Scheduled(fixedRate = 300000)
    public void scanForDeals() {
        System.out.println("Scanning for new deals...");

        // Mock logic: randomly decide if we found a new deal
        if (random.nextBoolean()) {
            // First get a valid card id from the DB
            List<Long> cardIds = jdbcTemplate.queryForList("SELECT id FROM card", Long.class);
            if (cardIds.isEmpty()) {
                 System.out.println("No cards found in DB, skipping deal generation.");
                 return;
            }
            Long cardId = cardIds.get(random.nextInt(cardIds.size()));

            BigDecimal trendPrice = BigDecimal.valueOf(10.00 + random.nextDouble() * 40.00); // 10 to 50
            // Deal is at least 15% cheaper (so price is at most 85% of trend)
            BigDecimal maxDealPrice = trendPrice.multiply(BigDecimal.valueOf(0.85));
            BigDecimal actualPrice = maxDealPrice.subtract(BigDecimal.valueOf(random.nextDouble() * 2.0)); // even cheaper

            // Calculate actual savings %
            BigDecimal savings = trendPrice.subtract(actualPrice);
            BigDecimal savingsPercentage = savings.divide(trendPrice, 4, java.math.RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

            CurrentDeal newDeal = new CurrentDeal(
                    cardId, // valid card ID
                    (long) (random.nextInt(1000) + 1), // random article ID
                    "Boutique_FR_" + random.nextInt(10), // Prioritize FR sellers
                    "FR",
                    "NM",
                    "FR",
                    false,
                    actualPrice,
                    trendPrice,
                    savingsPercentage
            );

            currentDealRepository.save(newDeal);
            System.out.println("Found a new deal! Savings: " + savingsPercentage + "%");
        }
    }
}
