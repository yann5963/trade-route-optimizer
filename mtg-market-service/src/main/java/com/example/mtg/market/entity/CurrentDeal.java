package com.example.mtg.market.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "current_deals")
public class CurrentDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "seller_name", nullable = false)
    private String sellerName;

    @Column(name = "seller_country", nullable = false)
    private String sellerCountry;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private String language;

    @Column(name = "is_foil")
    private Boolean isFoil = false;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "trend_price", nullable = false)
    private BigDecimal trendPrice;

    @Column(name = "savings_percentage", nullable = false)
    private BigDecimal savingsPercentage;

    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(nullable = false)
    private String status = "ACTIVE";

    public CurrentDeal() {}

    public CurrentDeal(Long cardId, String sellerName, String sellerCountry, String condition, String language, Boolean isFoil, BigDecimal price, BigDecimal trendPrice, BigDecimal savingsPercentage) {
        this.cardId = cardId;
        this.sellerName = sellerName;
        this.sellerCountry = sellerCountry;
        this.condition = condition;
        this.language = language;
        this.isFoil = isFoil;
        this.price = price;
        this.trendPrice = trendPrice;
        this.savingsPercentage = savingsPercentage;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
