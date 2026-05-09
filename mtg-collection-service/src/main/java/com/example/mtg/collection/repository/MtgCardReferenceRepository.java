package com.example.mtg.collection.repository;

import com.example.mtg.collection.entity.MtgCardReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MtgCardReferenceRepository extends JpaRepository<MtgCardReference, Long> {
    List<MtgCardReference> findBySetCodeAndNameContainingIgnoreCase(String setCode, String name);
    List<MtgCardReference> findBySetCode(String setCode);
}
