package com.example.mtg.market.controller;

import com.example.mtg.market.service.CartOptimizerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market/optimize")
public class OptimizerController {

    private final CartOptimizerService cartOptimizerService;

    public OptimizerController(CartOptimizerService cartOptimizerService) {
        this.cartOptimizerService = cartOptimizerService;
    }

    @PostMapping
    public List<Map<String, Object>> optimize(@RequestBody List<String> cardNames) {
        return cartOptimizerService.optimizeCart(cardNames);
    }
}
