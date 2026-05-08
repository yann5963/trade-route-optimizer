package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.entity.WishlistCard;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.UserCardRepository;
import com.example.mtg.collection.repository.WishlistCardRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling UI-related requests and rendering Thymeleaf templates.
 */
@Controller
@RequestMapping("/ui")
public class UIController {

    private final UserCardRepository userCardRepository;
    private final WishlistCardRepository wishlistCardRepository;
    private final CardRepository cardRepository;
    private final RestTemplate restTemplate;

    public UIController(UserCardRepository userCardRepository, 
                        WishlistCardRepository wishlistCardRepository,
                        CardRepository cardRepository) {
        this.userCardRepository = userCardRepository;
        this.wishlistCardRepository = wishlistCardRepository;
        this.cardRepository = cardRepository;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/collection")
    public String getCollectionDashboard(Model model) {
        List<UserCard> userCards = userCardRepository.findAll();
        model.addAttribute("userCards", userCards);
        return "views/collection";
    }

    @GetMapping("/wishlist")
    public String getWishlistDashboard(Model model) {
        List<WishlistCard> wishlistCards = wishlistCardRepository.findAll();
        model.addAttribute("wishlistCards", wishlistCards);
        return "views/wishlist";
    }

    @GetMapping("/collection/filter")
    public String filterCollection(@RequestParam(name = "filter", required = false, defaultValue = "") String filter, Model model) {
        List<UserCard> allCards = userCardRepository.findAll();

        List<UserCard> filteredCards = allCards.stream()
                .filter(uc -> uc.getCard().getName().toLowerCase().contains(filter.toLowerCase()) ||
                              uc.getCard().getSetName().toLowerCase().contains(filter.toLowerCase()))
                .collect(Collectors.toList());

        model.addAttribute("userCards", filteredCards);
        return "views/collection :: cardRows";
    }

    @GetMapping("/market")
    public String getMarketDashboard(Model model) {
        return "views/market-search";
    }

    @GetMapping("/settings")
    public String getSettingsPage(Model model) {
        return "views/settings";
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
        try {
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
        return "views/deals";
    }

    @PostMapping("/deals/{id}/ignore")
    @ResponseBody
    public String ignoreDeal(@PathVariable Long id) {
        try {
            restTemplate.put("http://localhost:8082/api/deals/" + id + "/status?status=IGNORED", null);
        } catch (Exception e) {
        }
        return "";
    }

    @PostMapping("/deals/{id}/add")
    @ResponseBody
    public String addDeal(@PathVariable Long id) {
        try {
            restTemplate.put("http://localhost:8082/api/deals/" + id + "/status?status=CART", null);
        } catch (Exception e) {
        }
        return "";
    }

    @GetMapping("/deals/fragment")
    public String getDealsFragment(Model model) {
        try {
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
        return "fragments/deals-section :: deals-grid";
    }

    @GetMapping("/deals/count")
    @ResponseBody
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
        }
        return "";
    }

    @GetMapping("/collection/add")
    public String getAddCardForm(Model model) {
        return "fragments/add-card-modal :: add-card-form";
    }

    @PostMapping("/collection/add")
    public String addCard(@RequestParam("name") String name,
                          @RequestParam("setName") String setName,
                          @RequestParam("condition") String condition,
                          @RequestParam("language") String language,
                          @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
                          @RequestParam("quantity") Integer quantity,
                          @RequestParam(value = "purchasePrice", required = false) java.math.BigDecimal purchasePrice,
                          Model model) {
        
        Card card = cardRepository.findAll().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name) && c.getSetName().equalsIgnoreCase(setName))
                .findFirst()
                .orElseGet(() -> cardRepository.save(new Card(name, setName, "Common")));

        UserCard userCard = new UserCard(card, condition, language, isFoil, quantity, purchasePrice);
        userCardRepository.save(userCard);

        List<UserCard> userCards = userCardRepository.findAll();
        model.addAttribute("userCards", userCards);
        return "views/collection :: cardRows";
    }

}
