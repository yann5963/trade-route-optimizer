package com.example.mtg.collection.controller;

import com.example.mtg.collection.dto.DealResponseDTO;
import com.example.mtg.collection.service.MarketClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/ui")
@RequiredArgsConstructor
public class MarketUIController {

    private final MarketClient marketClient;

    @GetMapping("/market")
    public String getMarketDashboard(Model model) {
        return "views/market-search";
    }

    @PostMapping("/market/simulate")
    @ResponseBody
    public String simulateMarket(Model model) {
        return "<div class=\"p-4 mb-4 text-sm text-green-800 rounded-lg bg-green-50\" role=\"alert\">" +
                "<span class=\"font-medium\">Simulation réussie!</span> Achats simulés et ajoutés à la base de données (Mock)." +
                "</div>";
    }

    @GetMapping("/deals")
    public String getDealsDashboard(Model model) {
        List<DealResponseDTO> deals = marketClient.getDeals();
        model.addAttribute("deals", deals);
        return "views/deals";
    }

    @PostMapping("/deals/{id}/ignore")
    @ResponseBody
    public String ignoreDeal(@PathVariable Long id) {
        marketClient.ignoreDeal(id);
        return "";
    }

    @PostMapping("/deals/{id}/add")
    @ResponseBody
    public String addDeal(@PathVariable Long id) {
        marketClient.addDealToCart(id);
        return "";
    }

    @GetMapping("/deals/fragment")
    public String getDealsFragment(Model model) {
        List<DealResponseDTO> deals = marketClient.getDeals();
        model.addAttribute("deals", deals);
        return "fragments/deals-section :: deals-grid";
    }

    @GetMapping("/deals/count")
    @ResponseBody
    public String getDealsCount() {
        Long count = marketClient.getDealsCount();
        if (count != null && count > 0) {
            return "<span class=\"absolute top-3 right-3 flex h-3 w-3\">\n" +
                    "  <span class=\"animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75\"></span>\n" +
                    "  <span class=\"relative inline-flex rounded-full h-3 w-3 bg-red-500\"></span>\n" +
                    "</span>";
        }
        return "";
    }
}
