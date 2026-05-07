package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.repository.UserCardRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/ui")
public class UIController {

    private final UserCardRepository userCardRepository;

    public UIController(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
    }

    @GetMapping("/collection")
    public String getCollectionDashboard(Model model) {
        List<UserCard> userCards = userCardRepository.findAll();
        model.addAttribute("userCards", userCards);
        return "collection";
    }

    @GetMapping("/collection/filter")
    public String filterCollection(@RequestParam(name = "filter", required = false, defaultValue = "") String filter, Model model) {
        List<UserCard> allCards = userCardRepository.findAll();

        List<UserCard> filteredCards = allCards.stream()
                .filter(uc -> uc.getCard().getName().toLowerCase().contains(filter.toLowerCase()) ||
                              uc.getCard().getSetName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("userCards", filteredCards);

        // Return just the fragment for HTMX updates
        return "collection :: cardRows";
    }

    @GetMapping("/market")
    public String getMarketDashboard(Model model) {
        return "market-search";
    }

    @org.springframework.web.bind.annotation.PostMapping("/ai/chat")
    @org.springframework.web.bind.annotation.ResponseBody
    public String handleAiChat(@RequestParam("query") String query) {
        // Simulate a delay to show the loading indicator
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "<div class=\"flex items-start\">" +
               "<i data-lucide=\"bot\" class=\"w-5 h-5 text-indigo-400 mt-0.5 mr-3 flex-shrink-0\"></i>" +
               "<div>" +
               "<p class=\"text-white font-medium mb-1\">Réponse de l'IA pour : <span class=\"text-indigo-300\">\"" + query + "\"</span></p>" +
               "<p class=\"text-gray-400\">Ceci est une réponse simulée de l'orchestrateur IA. Dans la version finale, ceci interrogera le modèle via Spring AI.</p>" +
               "</div>" +
               "</div>" +
               "<script>lucide.createIcons();</script>"; // Re-init icons for the new HTML
    }
}
