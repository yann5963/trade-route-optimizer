package com.example.mtg.collection.repository;

import com.example.mtg.collection.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByNameIgnoreCaseAndSetNameIgnoreCase(String name, String setName);
}
