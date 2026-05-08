package com.example.mtg.collection.repository;

import com.example.mtg.collection.entity.WishlistCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistCardRepository extends JpaRepository<WishlistCard, Long> {
}
