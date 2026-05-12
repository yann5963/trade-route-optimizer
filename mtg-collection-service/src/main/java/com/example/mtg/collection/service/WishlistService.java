package com.example.mtg.collection.service;

import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.WishlistCard;
import com.example.mtg.collection.entity.WishlistCardPriceHistory;
import com.example.mtg.collection.repository.WishlistCardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistCardRepository wishlistCardRepository;

    @Transactional
    public WishlistCard addWishlistCard(Card card, String condition, String language, Boolean isFoil, Integer quantity, BigDecimal maxPrice, BigDecimal marketPrice, LocalDate marketPriceDate) {
        WishlistCard wishlistCard = new WishlistCard(card, condition, language, isFoil, quantity, maxPrice);

        if (marketPrice != null) {
            LocalDate date = marketPriceDate != null ? marketPriceDate : LocalDate.now();
            wishlistCard.getPriceHistory().add(new WishlistCardPriceHistory(wishlistCard, marketPrice, date));
        }

        return wishlistCardRepository.save(wishlistCard);
    }

    @Transactional
    public void updateWishlistCard(Long id, String condition, String language, Boolean isFoil, Integer quantity, BigDecimal maxPrice, BigDecimal marketPrice, LocalDate marketPriceDate) {
        wishlistCardRepository.findById(id).ifPresent(wishCard -> {
            wishCard.setCondition(condition);
            wishCard.setLanguage(language);
            wishCard.setIsFoil(isFoil);
            wishCard.setDesiredQuantity(quantity);
            wishCard.setMaxPrice(maxPrice);

            if (marketPrice != null) {
                LocalDate date = marketPriceDate != null ? marketPriceDate : LocalDate.now();
                wishCard.getPriceHistory().add(new WishlistCardPriceHistory(wishCard, marketPrice, date));
            }

            wishlistCardRepository.save(wishCard);
        });
    }
}
