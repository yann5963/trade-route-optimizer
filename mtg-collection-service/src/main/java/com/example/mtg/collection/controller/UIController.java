package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.repository.UserCardRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling UI-related requests and rendering Thymeleaf templates.
 * It also acts as an intermediary for HTMX requests, sometimes fetching data from other microservices.
 */
@Controller
@RequestMapping("/ui")
public class UIController {

    private final UserCardRepository userCardRepository;
    private final RestTemplate restTemplate;

    public UIController(UserCardRepository userCardRepository) {
        this.userCardRepository = userCardRepository;
        this.restTemplate = new RestTemplate();
    }

    /**
     * Renders the main collection dashboard view.
     *
     * @param model the Spring MVC model to which user cards are added
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/collection")
    public String getCollectionDashboard(Model model) {
        List<UserCard> userCards = userCardRepository.findAll();
        model.addAttribute("userCards", userCards);
        return "collection";
    }

    /**
     * Filters the user's collection based on a search string and returns an HTML fragment.
     * This endpoint is designed to be called via HTMX.
     *
     * @param filter the search string used to filter card names or set names
     * @param model  the Spring MVC model to which filtered cards are added
     * @return the Thymeleaf fragment name to render the updated table rows
     */
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

    /**
     * Renders the market tracking dashboard view.
     *
     * @param model the Spring MVC model
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/market")
    public String getMarketDashboard(Model model) {
        return "market-search";
    }

    @GetMapping("/settings")
    public String getSettingsPage(Model model) {
        return "settings";
    }

    /**
     * Renders the deals dashboard view, fetching active deals from the market service.
     *
     * @param model the Spring MVC model to which active deals are added
     * @return the name of the Thymeleaf template to render
     */
    @GetMapping("/deals")
    public String getDealsDashboard(Model model) {
        try {
            // Call market-service to get active deals
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    "http://localhost:8082/api/deals",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            );
            model.addAttribute("deals", response.getBody());
        } catch (Exception e) {
            model.addAttribute("deals", new ArrayList<>());
        }
        return "deals";
    }

    /**
     * Handles the action to ignore a specific deal.
     * This endpoint is called via HTMX and communicates with the market service.
     *
     * @param id the ID of the deal to ignore
     * @return an empty string to remove the element from the DOM via HTMX swap
     */
    @org.springframework.web.bind.annotation.PostMapping("/deals/{id}/ignore")
    @org.springframework.web.bind.annotation.ResponseBody
    public String ignoreDeal(@org.springframework.web.bind.annotation.PathVariable Long id) {
        try {
            restTemplate.put("http://localhost:8082/api/deals/" + id + "/status?status=IGNORED", null);
        } catch (Exception e) {
            // Ignore if market-service is down
        }
        return "";
    }

    /**
     * Handles the action to add a specific deal to the cart.
     * This endpoint is called via HTMX and communicates with the market service.
     *
     * @param id the ID of the deal to add
     * @return an empty string to remove the element from the DOM via HTMX swap
     */
    @org.springframework.web.bind.annotation.PostMapping("/deals/{id}/add")
    @org.springframework.web.bind.annotation.ResponseBody
    public String addDeal(@org.springframework.web.bind.annotation.PathVariable Long id) {
        try {
            restTemplate.put("http://localhost:8082/api/deals/" + id + "/status?status=CART", null);
        } catch (Exception e) {
            // Ignore if market-service is down
        }
        return "";
    }

    /**
     * Fetches active deals from the market service and returns an HTML fragment.
     * This endpoint is designed to be called via HTMX for periodic polling.
     *
     * @param model the Spring MVC model to which active deals are added
     * @return the Thymeleaf fragment name to render the updated deals grid
     */
    @GetMapping("/deals/fragment")
    public String getDealsFragment(Model model) {
        try {
            // Call market-service to get active deals
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    "http://localhost:8082/api/deals",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {}
            );
            model.addAttribute("deals", response.getBody());
        } catch (Exception e) {
            model.addAttribute("deals", new ArrayList<>());
        }
        return "deals-section :: deals-grid";
    }

    /**
     * Fetches the count of active deals from the market service and returns an HTML fragment for a notification badge.
     * This endpoint is designed to be called via HTMX for periodic polling.
     *
     * @return an HTML string representing the notification badge if count > 0, otherwise an empty string
     */
    @GetMapping("/deals/count")
    @org.springframework.web.bind.annotation.ResponseBody
    public String getDealsCount() {
        try {
            Long count = restTemplate.getForObject("http://localhost:8082/api/deals/count", Long.class);
            if (count != null && count > 0) {
                return "<span class=\"absolute top-3 right-3 flex h-3 w-3\">\n" +
                       "  <span class=\"animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75\"></span>\n" +
                       "  <span class=\"relative inline-flex rounded-full h-3 w-3 bg-red-500\"></span>\n" +
                       "</span>";
            }
        } catch (Exception e) {
            // Ignore if market-service is down
        }
        return "";
    }

    /**
     * Handles natural language queries submitted to the AI assistant.
     * This is currently a mock implementation that simulates a delay and returns a static HTML response.
     *
     * @param query the natural language query from the user
     * @return an HTML string representing the AI's response
     */
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
