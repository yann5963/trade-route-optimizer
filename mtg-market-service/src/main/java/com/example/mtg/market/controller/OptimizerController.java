package com.example.mtg.market.controller;

import com.example.mtg.market.service.CartOptimizerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.mtg.market.dto.ArticleToBuy;
import com.example.mtg.market.service.CartSyncService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/market")
public class OptimizerController {

    private final CartOptimizerService cartOptimizerService;
    private final CartSyncService cartSyncService;

    public OptimizerController(CartOptimizerService cartOptimizerService, CartSyncService cartSyncService) {
        this.cartOptimizerService = cartOptimizerService;
        this.cartSyncService = cartSyncService;
    }

    @PostMapping(value = "/cart/sync", produces = MediaType.TEXT_HTML_VALUE)
    public String syncCart(@RequestBody Map<String, List<ArticleToBuy>> payload) {
        List<ArticleToBuy> articles = payload.get("articles");
        boolean success = cartSyncService.syncCart(articles);
        if (success) {
            return "<div class=\"p-4 mb-4 text-sm text-green-800 rounded-lg bg-green-50\" role=\"alert\">" +
                   "<span class=\"font-medium\">Succès!</span> Cartes ajoutées à votre panier Cardmarket !" +
                   "</div>" +
                   "<script>document.getElementById('deals-container').innerHTML = '';</script>";
        } else {
            return "<div class=\"p-4 mb-4 text-sm text-red-800 rounded-lg bg-red-50\" role=\"alert\">" +
                   "<span class=\"font-medium\">Erreur!</span> Article non disponible ou problème de synchronisation." +
                   "</div>";
        }
    }
}
