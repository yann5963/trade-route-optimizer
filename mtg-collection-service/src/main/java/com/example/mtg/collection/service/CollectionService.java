package com.example.mtg.collection.service;

import com.example.mtg.collection.dto.CollectionStatsDTO;
import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.MtgSet;
import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.MtgCardReferenceRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.UserCardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final UserCardRepository userCardRepository;
    private final CardRepository cardRepository;
    private final MtgSetRepository mtgSetRepository;
    private final MtgCardReferenceRepository mtgCardReferenceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public CollectionStatsDTO getStats(Specification<UserCard> spec) {
        BigDecimal totalInvestment = sumField(spec, "purchasePrice");
        BigDecimal totalSales = sumField(spec, "sellingPrice");
        BigDecimal balance = totalSales.subtract(totalInvestment);

        return new CollectionStatsDTO(totalInvestment, totalSales, balance);
    }

    private BigDecimal sumField(Specification<UserCard> spec, String fieldName) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        Root<UserCard> root = query.from(UserCard.class);

        // Calculate sum(field * quantity)
        // Note: Use coalesce to handle null values in multiplication if necessary, 
        // but typically SUM of nulls is null, so we wrap the whole result.
        Expression<BigDecimal> multiplied = cb.prod(root.get(fieldName), root.get("quantity").as(BigDecimal.class));
        query.select(cb.sum(multiplied));

        if (spec != null) {
            Predicate predicate = spec.toPredicate(root, query, cb);
            if (predicate != null) {
                query.where(predicate);
            }
        }

        BigDecimal result = entityManager.createQuery(query).getSingleResult();
        return result != null ? result : BigDecimal.ZERO;
    }

    public String resolveSetCode(String setIdentifier) {
        if (setIdentifier == null || setIdentifier.isEmpty()) return setIdentifier;
        return mtgSetRepository.findByCode(setIdentifier)
                .map(MtgSet::getCode)
                .or(() -> mtgSetRepository.findByNameIgnoreCase(setIdentifier)
                        .map(MtgSet::getCode))
                .orElse(setIdentifier);
    }

    public String resolveRarity(String name, String setCode, String fallback) {
        String searchName = name.contains("//") ? name.split("//")[0].trim() : name.trim();
        return mtgCardReferenceRepository
                .findFirstByNameIgnoreCaseAndSetCodeIgnoreCase(searchName, setCode)
                .map(ref -> ref.getRarity() != null ? ref.getRarity() : fallback)
                .or(() -> mtgCardReferenceRepository
                        .findFirstByNameContainingIgnoreCaseAndSetCodeIgnoreCase(name, setCode)
                        .map(ref -> ref.getRarity() != null ? ref.getRarity() : fallback))
                .orElse(fallback);
    }

    @Transactional
    public Card fixCardRarityIfNeeded(Card card, String name, String setCode) {
        if ("Common".equals(card.getRarity())) {
            String correctRarity = resolveRarity(name, setCode, "Common");
            if (!"Common".equals(correctRarity)) {
                card.setRarity(correctRarity);
                return cardRepository.save(card);
            }
        }
        return card;
    }

    @Transactional
    public UserCard addCard(Card card, String condition, String language, Boolean isFoil, Integer quantity, BigDecimal purchasePrice, BigDecimal sellingPrice) {
        UserCard userCard = new UserCard(card, condition, language, isFoil, quantity, purchasePrice, sellingPrice);
        return userCardRepository.save(userCard);
    }

    @Transactional
    public void updateCard(Long id, String condition, String language, Boolean isFoil, Integer quantity, BigDecimal purchasePrice, BigDecimal sellingPrice) {
        userCardRepository.findById(id).ifPresent(userCard -> {
            userCard.setCondition(condition);
            userCard.setLanguage(language);
            userCard.setIsFoil(isFoil);
            userCard.setQuantity(quantity);
            userCard.setPurchasePrice(purchasePrice);
            userCard.setSellingPrice(sellingPrice);
            userCardRepository.save(userCard);
        });
    }
}
