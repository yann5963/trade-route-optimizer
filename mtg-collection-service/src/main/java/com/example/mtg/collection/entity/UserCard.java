package com.example.mtg.collection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "user_card")
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(nullable = false)
    private String condition;

    @Column(nullable = false)
    private String language;

    @Column(name = "is_foil")
    private Boolean isFoil = false;

    private Integer quantity = 1;

    @Column(name = "purchase_price")
    private java.math.BigDecimal purchasePrice;

    public UserCard() {}

    public UserCard(Card card, String condition, String language, Boolean isFoil, Integer quantity, java.math.BigDecimal purchasePrice) {
        this.card = card;
        this.condition = condition;
        this.language = language;
        this.isFoil = isFoil;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public java.math.BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(java.math.BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
