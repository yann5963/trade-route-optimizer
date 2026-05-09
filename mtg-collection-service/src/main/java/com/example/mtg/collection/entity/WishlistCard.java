package com.example.mtg.collection.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.example.mtg.collection.entity.Card;

@Entity
@Table(name = "wishlist_card")
public class WishlistCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(targetEntity = Card.class)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private String language;

    @Column(name = "is_foil")
    private Boolean isFoil = false;

    @Column(name = "desired_quantity")
    private Integer desiredQuantity = 1;

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @OneToMany(mappedBy = "wishlistCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WishlistCardPriceHistory> priceHistory = new ArrayList<>();

    public WishlistCard() {}

    public WishlistCard(Card card, String condition, String language, Boolean isFoil, Integer desiredQuantity, BigDecimal maxPrice) {
        this.card = card;
        this.condition = condition;
        this.language = language;
        this.isFoil = isFoil;
        this.desiredQuantity = desiredQuantity;
        this.maxPrice = maxPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getIsFoil() {
        return isFoil;
    }

    public void setIsFoil(Boolean foil) {
        isFoil = foil;
    }

    public Integer getDesiredQuantity() {
        return desiredQuantity;
    }

    public void setDesiredQuantity(Integer desiredQuantity) {
        this.desiredQuantity = desiredQuantity;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<WishlistCardPriceHistory> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<WishlistCardPriceHistory> priceHistory) {
        this.priceHistory = priceHistory;
    }

    public WishlistCardPriceHistory getLatestPriceHistory() {
        if (priceHistory == null || priceHistory.isEmpty()) {
            return null;
        }
        return priceHistory.stream()
                .max(Comparator.comparing(WishlistCardPriceHistory::getRecordDate))
                .orElse(null);
    }

    public List<WishlistCardPriceHistory> getRecentPriceHistory(int count) {
        if (priceHistory == null) return new ArrayList<>();
        return priceHistory.stream()
                .sorted(Comparator.comparing(WishlistCardPriceHistory::getRecordDate).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
