package com.example.mtg.market.repository;

import com.example.mtg.market.entity.CurrentDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrentDealRepository extends JpaRepository<CurrentDeal, Long> {
    List<CurrentDeal> findByStatusOrderBySavingsPercentageDesc(String status);
}
