package com.example.mtg.collection.repository;

import com.example.mtg.collection.entity.MtgSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MtgSetRepository extends JpaRepository<MtgSet, String> {
    Optional<MtgSet> findByCode(String code);
    Optional<MtgSet> findByNameIgnoreCase(String name);
}
