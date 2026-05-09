package com.example.mtg.collection.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_status")
public class SyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;

    @Column(name = "card_count")
    private Long cardCount;

    @Column(name = "set_count")
    private Long setCount;

    public SyncStatus() {}

    public SyncStatus(LocalDateTime lastSyncDate, Long cardCount, Long setCount) {
        this.lastSyncDate = lastSyncDate;
        this.cardCount = cardCount;
        this.setCount = setCount;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getLastSyncDate() { return lastSyncDate; }
    public void setLastSyncDate(LocalDateTime lastSyncDate) { this.lastSyncDate = lastSyncDate; }

    public Long getCardCount() { return cardCount; }
    public void setCardCount(Long cardCount) { this.cardCount = cardCount; }

    public Long getSetCount() { return setCount; }
    public void setSetCount(Long setCount) { this.setCount = setCount; }
}
