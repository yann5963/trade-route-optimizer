package com.example.mtg.market.controller;

import com.example.mtg.market.dto.DealDTO;
import com.example.mtg.market.repository.CurrentDealRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/deals")
public class DealController {

    private final CurrentDealRepository currentDealRepository;

    private final org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    public DealController(CurrentDealRepository currentDealRepository, org.springframework.jdbc.core.JdbcTemplate jdbcTemplate) {
        this.currentDealRepository = currentDealRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping
    public List<DealDTO> getActiveDeals() {
        return currentDealRepository.findByStatusOrderBySavingsPercentageDesc("ACTIVE")
                .stream()
                .map(deal -> {
                    String cardName = "Unknown Card Name";
                    try {
                        cardName = jdbcTemplate.queryForObject(
                            "SELECT name FROM card WHERE id = ?",
                            String.class,
                            deal.getCardId()
                        );
                    } catch (Exception e) {
                        // Card not found or other DB error
                    }

                    return new DealDTO(
                        deal.getId(),
                        deal.getCardId(),
                        deal.getArticleId(),
                        cardName,
                        deal.getSellerName(),
                        deal.getSellerCountry(),
                        deal.getCondition(),
                        deal.getLanguage(),
                        deal.getIsFoil(),
                        deal.getPrice(),
                        deal.getTrendPrice(),
                        deal.getSavingsPercentage(),
                        deal.getDetectedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/count")
    public long getActiveDealsCount() {
        return currentDealRepository.findByStatusOrderBySavingsPercentageDesc("ACTIVE").size();
    }

    @PutMapping("/{id}/status")
    public void updateDealStatus(@PathVariable Long id, @RequestParam String status) {
        currentDealRepository.findById(id).ifPresent(deal -> {
            deal.setStatus(status);
            currentDealRepository.save(deal);
        });
    }
}
