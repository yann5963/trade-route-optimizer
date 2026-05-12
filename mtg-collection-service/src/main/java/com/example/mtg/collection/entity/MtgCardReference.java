package com.example.mtg.collection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mtg_card_reference")
public class MtgCardReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "scryfall_id", nullable = false, unique = true)
    private String scryfallId;

    @Column(name = "set_code", nullable = false)
    private String setCode;

    @Column(name = "rarity")
    private String rarity;

    public MtgCardReference() {}

    public MtgCardReference(String name, String scryfallId, String setCode, String rarity) {
        this.name = name;
        this.scryfallId = scryfallId;
        this.setCode = setCode;
        this.rarity = rarity;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getScryfallId() { return scryfallId; }
    public void setScryfallId(String scryfallId) { this.scryfallId = scryfallId; }

    public String getSetCode() { return setCode; }
    public void setSetCode(String setCode) { this.setCode = setCode; }

    public String getRarity() { return rarity; }
    public void setRarity(String rarity) { this.rarity = rarity; }
}
