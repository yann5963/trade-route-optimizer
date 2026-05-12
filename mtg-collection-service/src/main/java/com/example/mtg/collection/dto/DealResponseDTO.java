package com.example.mtg.collection.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DealResponseDTO {
    private Long id;
    private String cardName;
    private String sellerName;
    private String sellerCountry;
    private String condition;
    private String language;
    private Boolean isFoil;
    private BigDecimal price;
    private BigDecimal trendPrice;
    private Double savingsPercentage;
}
