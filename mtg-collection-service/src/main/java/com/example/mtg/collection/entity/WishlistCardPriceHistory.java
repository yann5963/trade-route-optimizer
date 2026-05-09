package com.example.mtg.collection.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "wishlist_card_price_history")
public class WishlistCardPriceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_card_id", nullable = false)
    private WishlistCard wishlistCard;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    public WishlistCardPriceHistory() {
    }

    public WishlistCardPriceHistory(WishlistCard wishlistCard, BigDecimal price, LocalDate recordDate) {
        this.wishlistCard = wishlistCard;
        this.price = price;
        this.recordDate = recordDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public WishlistCard getWishlistCard() {
        return wishlistCard;
    }

    public void setWishlistCard(WishlistCard wishlistCard) {
        this.wishlistCard = wishlistCard;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(LocalDate recordDate) {
        this.recordDate = recordDate;
    }
}
