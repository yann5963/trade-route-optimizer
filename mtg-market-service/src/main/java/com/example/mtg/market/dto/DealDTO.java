package com.example.mtg.market.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DealDTO {
    private Long id;
    private Long cardId;
    private String cardName; // Useful for UI without needing an extra fetch
    private String sellerName;
    private String sellerCountry;
    private String condition;
    private String language;
    private Boolean isFoil;
    private BigDecimal price;
    private BigDecimal trendPrice;
    private BigDecimal savingsPercentage;
    private LocalDateTime detectedAt;

    public DealDTO() {}

    public DealDTO(Long id, Long cardId, String cardName, String sellerName, String sellerCountry, String condition, String language, Boolean isFoil, BigDecimal price, BigDecimal trendPrice, BigDecimal savingsPercentage, LocalDateTime detectedAt) {
        this.id = id;
        this.cardId = cardId;
        this.cardName = cardName;
        this.sellerName = sellerName;
        this.sellerCountry = sellerCountry;
        this.condition = condition;
        this.language = language;
        this.isFoil = isFoil;
        this.price = price;
        this.trendPrice = trendPrice;
        this.savingsPercentage = savingsPercentage;
        this.detectedAt = detectedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getSellerCountry() { return sellerCountry; }
    public void setSellerCountry(String sellerCountry) { this.sellerCountry = sellerCountry; }
    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public Boolean getIsFoil() { return isFoil; }
    public void setIsFoil(Boolean foil) { isFoil = foil; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getTrendPrice() { return trendPrice; }
    public void setTrendPrice(BigDecimal trendPrice) { this.trendPrice = trendPrice; }
    public BigDecimal getSavingsPercentage() { return savingsPercentage; }
    public void setSavingsPercentage(BigDecimal savingsPercentage) { this.savingsPercentage = savingsPercentage; }
    public LocalDateTime getDetectedAt() { return detectedAt; }
    public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
}
