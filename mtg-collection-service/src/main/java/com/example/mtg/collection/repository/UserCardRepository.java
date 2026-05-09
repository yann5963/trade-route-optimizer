package com.example.mtg.collection.repository;

import com.example.mtg.collection.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard, Long>, JpaSpecificationExecutor<UserCard> {
    List<UserCard> findByCardId(Long cardId);
}
