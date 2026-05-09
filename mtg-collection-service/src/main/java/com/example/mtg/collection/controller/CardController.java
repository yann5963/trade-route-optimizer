package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.UserCardRepository;
import com.example.mtg.collection.repository.WishlistCardRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CardController {

    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;
    private final WishlistCardRepository wishlistCardRepository;

    public CardController(CardRepository cardRepository, 
                          UserCardRepository userCardRepository,
                          WishlistCardRepository wishlistCardRepository) {
        this.cardRepository = cardRepository;
        this.userCardRepository = userCardRepository;
        this.wishlistCardRepository = wishlistCardRepository;
    }

    @GetMapping("/cards")
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @PostMapping("/cards")
    public Card createCard(@RequestBody Card card) {
        return cardRepository.save(card);
    }

    @GetMapping("/user-cards")
    public List<UserCard> getAllUserCards() {
        return userCardRepository.findAll();
    }

    @PostMapping("/user-cards")
    public ResponseEntity<UserCard> createUserCard(@RequestBody UserCardRequest request) {
        return cardRepository.findById(request.getCardId())
                .map(card -> {
                    UserCard userCard = new UserCard(card, request.getCondition(), request.getLanguage(), request.getIsFoil(), request.getQuantity(), request.getPurchasePrice());
                    return ResponseEntity.ok(userCardRepository.save(userCard));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/user-cards/{id}")
    public ResponseEntity<Void> deleteUserCard(@PathVariable Long id) {
        userCardRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/wishlist-cards/{id}")
    public ResponseEntity<Void> deleteWishlistCard(@PathVariable Long id) {
        wishlistCardRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}

class UserCardRequest {
    private Long cardId;
    private String condition;
    private String language;
    private Boolean isFoil;
    private Integer quantity;
    private java.math.BigDecimal purchasePrice;

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
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

    public void setIsFoil(Boolean isFoil) {
        this.isFoil = isFoil;
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
