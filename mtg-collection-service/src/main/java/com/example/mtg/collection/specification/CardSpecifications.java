package com.example.mtg.collection.specification;

import com.example.mtg.collection.entity.Card;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CardSpecifications {

    public static <T> Specification<T> filterBy(String filter, String setCode, String foil, BigDecimal minPrice, BigDecimal maxPrice, String priceField) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Join<T, Card> cardJoin = root.join("card");

            if (filter != null && !filter.isEmpty()) {
                String lp = "%" + filter.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(cardJoin.get("name")), lp),
                        cb.like(cb.lower(cardJoin.get("setName")), lp)
                ));
            }

            if (setCode != null && !setCode.isEmpty()) {
                predicates.add(cb.or(
                        cb.equal(cardJoin.get("setName"), setCode),
                        cb.equal(cb.lower(cardJoin.get("setName")), setCode.toLowerCase())
                ));
            }

            if ("yes".equalsIgnoreCase(foil)) {
                predicates.add(cb.isTrue(root.get("isFoil")));
            } else if ("no".equalsIgnoreCase(foil)) {
                predicates.add(cb.isFalse(root.get("isFoil")));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(priceField), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(priceField), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
