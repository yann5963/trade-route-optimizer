package com.example.mtg.market.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardmarketProductResponse {
    private List<CardmarketProduct> product;

    public List<CardmarketProduct> getProduct() {
        return product;
    }

    public void setProduct(List<CardmarketProduct> product) {
        this.product = product;
    }
}
