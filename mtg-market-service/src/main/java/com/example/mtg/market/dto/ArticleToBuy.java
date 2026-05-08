package com.example.mtg.market.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArticleToBuy {
    @JsonProperty("idArticle")
    private Long idArticle;
    private int count;

    public ArticleToBuy() {}

    public ArticleToBuy(Long idArticle, int count) {
        this.idArticle = idArticle;
        this.count = count;
    }

    public Long getIdArticle() {
        return idArticle;
    }

    public void setIdArticle(Long idArticle) {
        this.idArticle = idArticle;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
