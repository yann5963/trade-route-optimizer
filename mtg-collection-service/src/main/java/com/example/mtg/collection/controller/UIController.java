package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.MtgSet;
import com.example.mtg.collection.entity.MtgCardReference;
import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.entity.WishlistCard;
import com.example.mtg.collection.entity.WishlistCardPriceHistory;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.SyncStatusRepository;
import com.example.mtg.collection.repository.UserCardRepository;
import com.example.mtg.collection.repository.WishlistCardRepository;
import com.example.mtg.collection.repository.MtgCardReferenceRepository;
import com.example.mtg.collection.entity.SyncStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsible for handling UI-related requests and rendering
 * Thymeleaf templates.
 */
@Controller
@RequestMapping("/ui")
public class UIController {

    @Value("${app.pagination.max-record-collection:6}")
    private int maxRecordCollection;

    @Value("${app.pagination.max-record-wishlist:6}")
    private int maxRecordWishlist;

    private final UserCardRepository userCardRepository;
    private final WishlistCardRepository wishlistCardRepository;
    private final CardRepository cardRepository;
    private final MtgSetRepository mtgSetRepository;
    private final SyncStatusRepository syncStatusRepository;
    private final MtgCardReferenceRepository mtgCardReferenceRepository;
    private final RestTemplate restTemplate;

    public UIController(UserCardRepository userCardRepository,
            WishlistCardRepository wishlistCardRepository,
            CardRepository cardRepository,
            MtgSetRepository mtgSetRepository,
            SyncStatusRepository syncStatusRepository,
            MtgCardReferenceRepository mtgCardReferenceRepository) {
        this.userCardRepository = userCardRepository;
        this.wishlistCardRepository = wishlistCardRepository;
        this.cardRepository = cardRepository;
        this.mtgSetRepository = mtgSetRepository;
        this.syncStatusRepository = syncStatusRepository;
        this.mtgCardReferenceRepository = mtgCardReferenceRepository;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/collection")
    public String getCollectionDashboard(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            Model model) {
        PageRequest pageRequest = PageRequest.of(page, maxRecordCollection,
                Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<UserCard> userCardPage = userCardRepository.findAll(pageRequest);

        model.addAttribute("userCards", userCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        
        List<UserCard> allCards = userCardRepository.findAll();
        calculateAndAddStats(allCards, model);
        
        return "views/collection";
    }

    private void calculateAndAddStats(List<UserCard> cards, Model model) {
        java.math.BigDecimal totalInvestment = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalSales = java.math.BigDecimal.ZERO;

        for (UserCard card : cards) {
            if (card.getPurchasePrice() != null) {
                totalInvestment = totalInvestment.add(card.getPurchasePrice().multiply(java.math.BigDecimal.valueOf(card.getQuantity())));
            }
            if (card.getSellingPrice() != null) {
                totalSales = totalSales.add(card.getSellingPrice().multiply(java.math.BigDecimal.valueOf(card.getQuantity())));
            }
        }
        java.math.BigDecimal balance = totalSales.subtract(totalInvestment);

        model.addAttribute("totalInvestment", totalInvestment);
        model.addAttribute("totalSales", totalSales);
        model.addAttribute("balance", balance);
    }

    @GetMapping("/wishlist")
    public String getWishlistDashboard(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            Model model) {
        PageRequest pageRequest = PageRequest.of(page, maxRecordWishlist,
                Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<WishlistCard> wishlistCardPage = wishlistCardRepository.findAll(pageRequest);

        model.addAttribute("wishlistCards", wishlistCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        return "views/wishlist";
    }

    @GetMapping("/collection/filter")
    public String filterCollection(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
            @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
            @RequestParam(name = "minPrice", required = false) java.math.BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, maxRecordCollection, sort);

        Specification<UserCard> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (!filter.isEmpty()) {
                String lp = "%" + filter.toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("card").get("name")), lp),
                        cb.like(cb.lower(root.get("card").get("setName")), lp)));
            }
            if (!setCode.isEmpty()) {
                predicates.add(cb.or(cb.equal(root.get("card").get("setName"), setCode),
                        cb.equal(cb.lower(root.get("card").get("setName")), setCode.toLowerCase())));
            }
            if ("yes".equalsIgnoreCase(foil))
                predicates.add(cb.isTrue(root.get("isFoil")));
            if ("no".equalsIgnoreCase(foil))
                predicates.add(cb.isFalse(root.get("isFoil")));
            if (minPrice != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchasePrice"), minPrice));
            if (maxPrice != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("purchasePrice"), maxPrice));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<UserCard> userCardPage = userCardRepository.findAll(spec, pageRequest);
        model.addAttribute("userCards", userCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "views/collection :: collectionTable";
    }

    @GetMapping("/collection/stats")
    public String getCollectionStats(
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
            @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
            @RequestParam(name = "minPrice", required = false) java.math.BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            Model model) {

        Specification<UserCard> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (!filter.isEmpty()) {
                String lp = "%" + filter.toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("card").get("name")), lp),
                        cb.like(cb.lower(root.get("card").get("setName")), lp)));
            }
            if (!setCode.isEmpty()) {
                predicates.add(cb.or(cb.equal(root.get("card").get("setName"), setCode),
                        cb.equal(cb.lower(root.get("card").get("setName")), setCode.toLowerCase())));
            }
            if ("yes".equalsIgnoreCase(foil))
                predicates.add(cb.isTrue(root.get("isFoil")));
            if ("no".equalsIgnoreCase(foil))
                predicates.add(cb.isFalse(root.get("isFoil")));
            if (minPrice != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("purchasePrice"), minPrice));
            if (maxPrice != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("purchasePrice"), maxPrice));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        List<UserCard> cards = userCardRepository.findAll(spec);
        calculateAndAddStats(cards, model);

        return "views/collection :: collectionStats";
    }

    @GetMapping("/wishlist/filter")
    public String filterWishlist(@RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
            @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
            @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
            @RequestParam(name = "minPrice", required = false) java.math.BigDecimal minPrice,
            @RequestParam(name = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, maxRecordWishlist, sort);

        Specification<WishlistCard> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();
            if (!filter.isEmpty()) {
                String lp = "%" + filter.toLowerCase() + "%";
                predicates.add(cb.or(cb.like(cb.lower(root.get("card").get("name")), lp),
                        cb.like(cb.lower(root.get("card").get("setName")), lp)));
            }
            if (!setCode.isEmpty()) {
                predicates.add(cb.or(cb.equal(root.get("card").get("setName"), setCode),
                        cb.equal(cb.lower(root.get("card").get("setName")), setCode.toLowerCase())));
            }
            if ("yes".equalsIgnoreCase(foil))
                predicates.add(cb.isTrue(root.get("isFoil")));
            if ("no".equalsIgnoreCase(foil))
                predicates.add(cb.isFalse(root.get("isFoil")));
            if (minPrice != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxPrice"), minPrice));
            if (maxPrice != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("maxPrice"), maxPrice));
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<WishlistCard> wishlistCardPage = wishlistCardRepository.findAll(spec, pageRequest);
        model.addAttribute("wishlistCards", wishlistCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "views/wishlist :: wishlistTable";
    }

    @GetMapping("/market")
    public String getMarketDashboard(Model model) {
        return "views/market-search";
    }

    @GetMapping("/settings")
    public String getSettingsPage(Model model) {
        SyncStatus status = syncStatusRepository.findTopByOrderByIdDesc().orElse(null);
        model.addAttribute("syncStatus", status);
        return "views/settings";
    }

    @PostMapping("/market/simulate")
    @ResponseBody
    public String simulateMarket(Model model) {
        return "<div class=\"p-4 mb-4 text-sm text-green-800 rounded-lg bg-green-50\" role=\"alert\">" +
                "<span class=\"font-medium\">Simulation réussie!</span> Achats simulés et ajoutés à la base de données (Mock)."
                +
                "</div>";
    }

    @GetMapping("/deals")
    public String getDealsDashboard(Model model) {
        try {
            ResponseEntity<List<Object>> response = restTemplate.exchange(
                    "http://localhost:8082/api/deals",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Object>>() {
                    });
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
                    new ParameterizedTypeReference<List<Object>>() {
                    });
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
                        "  <span class=\"animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75\"></span>\n"
                        +
                        "  <span class=\"relative inline-flex rounded-full h-3 w-3 bg-red-500\"></span>\n" +
                        "</span>";
            }
        } catch (Exception e) {
        }
        return "";
    }

    @GetMapping("/collection/add")
    public String getAddCardForm(Model model) {
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        return "fragments/add-card-modal :: add-card-form";
    }

    @GetMapping("/wishlist/add")
    public String getAddWishlistCardForm(Model model) {
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        return "fragments/add-wishlist-modal :: add-wishlist-form";
    }

    @PostMapping("/wishlist/add")
    public String addWishlistCard(@RequestParam("name") String name,
            @RequestParam("setName") String setName,
            @RequestParam("condition") String condition,
            @RequestParam("language") String language,
            @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "marketPrice", required = false) BigDecimal marketPrice,
            @RequestParam(value = "marketPriceDate", required = false) LocalDate marketPriceDate,
            Model model) {

        String resolvedSetCode = resolveSetCode(setName);

        Card card = cardRepository.findByNameIgnoreCaseAndSetNameIgnoreCase(name, resolvedSetCode)
                .orElseGet(() -> {
                    String rarity = mtgCardReferenceRepository
                            .findByNameIgnoreCaseAndSetCodeIgnoreCase(name, resolvedSetCode)
                            .map(ref -> ref.getRarity() != null ? ref.getRarity() : "Common")
                            .orElse("Common");
                    return cardRepository.save(new Card(name, resolvedSetCode, rarity));
                });

        WishlistCard wishlistCard = new WishlistCard(card, condition, language, isFoil, quantity, maxPrice);
        
        if (marketPrice != null) {
            LocalDate date = marketPriceDate != null ? marketPriceDate : LocalDate.now();
            wishlistCard.getPriceHistory().add(new WishlistCardPriceHistory(wishlistCard, marketPrice, date));
        }
        
        wishlistCardRepository.save(wishlistCard);

        return filterWishlist(page, sortBy, direction, "", "", "all", null, null, model);
    }

    @PostMapping("/collection/add")
    public String addCard(@RequestParam("name") String name,
            @RequestParam("setName") String setName,
            @RequestParam("condition") String condition,
            @RequestParam("language") String language,
            @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "purchasePrice", required = false) java.math.BigDecimal purchasePrice,
            @RequestParam(value = "sellingPrice", required = false) java.math.BigDecimal sellingPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            Model model,
            HttpServletResponse response) {
        
        response.setHeader("HX-Trigger", "updateStats");

        String resolvedSetCode = resolveSetCode(setName);

        Card card = cardRepository.findByNameIgnoreCaseAndSetNameIgnoreCase(name, resolvedSetCode)
                .orElseGet(() -> {
                    String rarity = mtgCardReferenceRepository
                            .findByNameIgnoreCaseAndSetCodeIgnoreCase(name, resolvedSetCode)
                            .map(ref -> ref.getRarity() != null ? ref.getRarity() : "Common")
                            .orElse("Common");
                    return cardRepository.save(new Card(name, resolvedSetCode, rarity));
                });

        UserCard userCard = new UserCard(card, condition, language, isFoil, quantity, purchasePrice, sellingPrice);
        userCardRepository.save(userCard);

        return filterCollection(page, sortBy, direction, "", "", "all", null, null, model);
    }

    @GetMapping("/collection/edit/{id}")
    public String getEditCardForm(@PathVariable Long id, Model model) {
        userCardRepository.findById(id).ifPresent(userCard -> model.addAttribute("userCard", userCard));
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        return "fragments/edit-card-modal :: edit-card-form";
    }

    @PostMapping("/collection/edit/{id}")
    public String updateCard(@PathVariable Long id,
            @RequestParam("condition") String condition,
            @RequestParam("language") String language,
            @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "purchasePrice", required = false) java.math.BigDecimal purchasePrice,
            @RequestParam(value = "sellingPrice", required = false) java.math.BigDecimal sellingPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            Model model,
            HttpServletResponse response) {
        
        response.setHeader("HX-Trigger", "updateStats");
        userCardRepository.findById(id).ifPresent(userCard -> {
            userCard.setCondition(condition);
            userCard.setLanguage(language);
            userCard.setIsFoil(isFoil);
            userCard.setQuantity(quantity);
            userCard.setPurchasePrice(purchasePrice);
            userCard.setSellingPrice(sellingPrice);
            userCardRepository.save(userCard);
        });

        return filterCollection(page, sortBy, direction, "", "", "all", null, null, model);
    }

    @GetMapping("/wishlist/edit/{id}")
    public String getEditWishlistCardForm(@PathVariable Long id, Model model) {
        wishlistCardRepository.findById(id).ifPresent(wishCard -> model.addAttribute("wishCard", wishCard));
        model.addAttribute("mtgSets", mtgSetRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "name")));
        return "fragments/edit-wishlist-modal :: edit-wishlist-form";
    }

    @PostMapping("/wishlist/edit/{id}")
    public String updateWishlistCard(@PathVariable Long id,
            @RequestParam("condition") String condition,
            @RequestParam("language") String language,
            @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
            @RequestParam("quantity") Integer quantity,
            @RequestParam(value = "maxPrice", required = false) java.math.BigDecimal maxPrice,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
            @RequestParam(name = "direction", defaultValue = "ASC") String direction,
            @RequestParam(value = "marketPrice", required = false) BigDecimal marketPrice,
            @RequestParam(value = "marketPriceDate", required = false) LocalDate marketPriceDate,
            Model model) {
        wishlistCardRepository.findById(id).ifPresent(wishCard -> {
            wishCard.setCondition(condition);
            wishCard.setLanguage(language);
            wishCard.setIsFoil(isFoil);
            wishCard.setDesiredQuantity(quantity);
            wishCard.setMaxPrice(maxPrice);
            
            if (marketPrice != null) {
                LocalDate date = marketPriceDate != null ? marketPriceDate : LocalDate.now();
                wishCard.getPriceHistory().add(new WishlistCardPriceHistory(wishCard, marketPrice, date));
            }
            
            wishlistCardRepository.save(wishCard);
        });

        return filterWishlist(page, sortBy, direction, "", "", "all", null, null, model);
    }

    private String resolveSetCode(String setIdentifier) {
        if (setIdentifier == null || setIdentifier.isEmpty()) return setIdentifier;
        return mtgSetRepository.findByCode(setIdentifier)
                .map(MtgSet::getCode)
                .or(() -> mtgSetRepository.findByNameIgnoreCase(setIdentifier)
                        .map(MtgSet::getCode))
                .orElse(setIdentifier);
    }

}
