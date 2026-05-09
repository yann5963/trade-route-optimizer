package com.example.mtg.collection.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "mtg_set")
public class MtgSet {

    @Id
    private String id; // Scryfall ID

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(name = "icon_uri")
    private String iconUri;

    public MtgSet() {}

    public MtgSet(String id, String code, String name, String iconUri) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.iconUri = iconUri;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIconUri() { return iconUri; }
    public void setIconUri(String iconUri) { this.iconUri = iconUri; }
}
