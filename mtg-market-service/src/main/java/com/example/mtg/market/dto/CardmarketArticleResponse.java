package com.example.mtg.market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardmarketArticleResponse {
    private List<CardmarketArticle> article;

    public List<CardmarketArticle> getArticle() {
        return article;
    }

    public void setArticle(List<CardmarketArticle> article) {
        this.article = article;
    }
}
