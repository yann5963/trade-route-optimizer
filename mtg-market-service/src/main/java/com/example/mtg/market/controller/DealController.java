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

    public DealController(CurrentDealRepository currentDealRepository) {
        this.currentDealRepository = currentDealRepository;
    }

    @GetMapping
    public List<DealDTO> getActiveDeals() {
        return currentDealRepository.findByStatusOrderBySavingsPercentageDesc("ACTIVE")
                .stream()
                .map(deal -> new DealDTO(
                        deal.getId(),
                        deal.getCardId(),
                        "Unknown Card Name", // In a real app, join with card catalog
                        deal.getSellerName(),
                        deal.getSellerCountry(),
                        deal.getCondition(),
                        deal.getLanguage(),
                        deal.getIsFoil(),
                        deal.getPrice(),
                        deal.getTrendPrice(),
                        deal.getSavingsPercentage(),
                        deal.getDetectedAt()
                ))
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
