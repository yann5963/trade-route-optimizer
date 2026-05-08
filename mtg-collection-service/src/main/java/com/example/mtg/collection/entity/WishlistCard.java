package com.example.mtg.collection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "wishlist_card")
public class WishlistCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @Column(name = "desired_quantity")
    private Integer desiredQuantity = 1;

    public WishlistCard() {}

    public WishlistCard(Card card, Integer desiredQuantity) {
        this.card = card;
        this.desiredQuantity = desiredQuantity;
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

    public Integer getDesiredQuantity() {
        return desiredQuantity;
    }

    public void setDesiredQuantity(Integer desiredQuantity) {
        this.desiredQuantity = desiredQuantity;
    }
}
