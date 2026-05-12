package com.example.mtg.collection.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionStatsDTO {
    private BigDecimal totalInvestment;
    private BigDecimal totalSales;
    private BigDecimal balance;
}
