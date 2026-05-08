package com.example.mtg.market.dto;

import java.util.List;

public class CartRequest {
    private List<ArticleToBuy> article;

    public CartRequest() {}

    public CartRequest(List<ArticleToBuy> article) {
        this.article = article;
    }

    public List<ArticleToBuy> getArticle() {
        return article;
    }

    public void setArticle(List<ArticleToBuy> article) {
        this.article = article;
    }
}
