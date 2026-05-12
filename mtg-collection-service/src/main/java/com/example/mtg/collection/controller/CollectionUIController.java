package com.example.mtg.collection.controller;

import com.example.mtg.collection.dto.CollectionStatsDTO;
import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.UserCard;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.UserCardRepository;
import com.example.mtg.collection.service.CollectionService;
import com.example.mtg.collection.specification.CardSpecifications;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/ui/collection")
@RequiredArgsConstructor
public class CollectionUIController {

    @Value("${app.pagination.max-record-collection:6}")
    private int maxRecordCollection;

    private final UserCardRepository userCardRepository;
    private final CardRepository cardRepository;
    private final MtgSetRepository mtgSetRepository;
    private final CollectionService collectionService;

    @GetMapping
    public String getCollectionDashboard(@RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                         @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                         Model model) {
        PageRequest pageRequest = PageRequest.of(page, maxRecordCollection, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<UserCard> userCardPage = userCardRepository.findAll(pageRequest);

        model.addAttribute("userCards", userCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));

        CollectionStatsDTO stats = collectionService.getStats(null);
        model.addAttribute("totalInvestment", stats.getTotalInvestment());
        model.addAttribute("totalSales", stats.getTotalSales());
        model.addAttribute("balance", stats.getBalance());

        return "views/collection";
    }

    @GetMapping("/filter")
    public String filterCollection(@RequestParam(name = "page", defaultValue = "0") int page,
                                   @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                   @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                   @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                   @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
                                   @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
                                   @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                   @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                   Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, maxRecordCollection, sort);

        Specification<UserCard> spec = CardSpecifications.filterBy(filter, setCode, foil, minPrice, maxPrice, "purchasePrice");

        Page<UserCard> userCardPage = userCardRepository.findAll(spec, pageRequest);
        model.addAttribute("userCards", userCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "views/collection :: collectionTable";
    }

    @GetMapping("/stats")
    public String getCollectionStats(@RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                     @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
                                     @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
                                     @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                     @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                     Model model) {

        Specification<UserCard> spec = CardSpecifications.filterBy(filter, setCode, foil, minPrice, maxPrice, "purchasePrice");
        CollectionStatsDTO stats = collectionService.getStats(spec);

        model.addAttribute("totalInvestment", stats.getTotalInvestment());
        model.addAttribute("totalSales", stats.getTotalSales());
        model.addAttribute("balance", stats.getBalance());

        return "views/collection :: collectionStats";
    }

    @GetMapping("/add")
    public String getAddCardForm(Model model) {
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "fragments/add-card-modal :: add-card-form";
    }

    @PostMapping("/add")
    public String addCard(@RequestParam("name") String name,
                          @RequestParam("setName") String setName,
                          @RequestParam(value = "rarity", required = false, defaultValue = "Common") String rarityParam,
                          @RequestParam("condition") String condition,
                          @RequestParam("language") String language,
                          @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
                          @RequestParam("quantity") Integer quantity,
                          @RequestParam(value = "purchasePrice", required = false) BigDecimal purchasePrice,
                          @RequestParam(value = "sellingPrice", required = false) BigDecimal sellingPrice,
                          @RequestParam(name = "page", defaultValue = "0") int page,
                          @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                          @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                          Model model,
                          HttpServletResponse response) {

        response.setHeader("HX-Trigger", "updateStats");

        String resolvedSetCode = collectionService.resolveSetCode(setName);

        Card card = cardRepository.findByNameIgnoreCaseAndSetNameIgnoreCase(name, resolvedSetCode)
                .map(existingCard -> collectionService.fixCardRarityIfNeeded(existingCard, name, resolvedSetCode))
                .orElseGet(() -> {
                    String rarity = collectionService.resolveRarity(name, resolvedSetCode, rarityParam);
                    return cardRepository.save(new Card(name, resolvedSetCode, rarity));
                });

        collectionService.addCard(card, condition, language, isFoil, quantity, purchasePrice, sellingPrice);

        return filterCollection(page, sortBy, direction, "", "", "all", null, null, model);
    }

    @GetMapping("/edit/{id}")
    public String getEditCardForm(@PathVariable Long id, Model model) {
        userCardRepository.findById(id).ifPresent(userCard -> model.addAttribute("userCard", userCard));
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "fragments/edit-card-modal :: edit-card-form";
    }

    @PostMapping("/edit/{id}")
    public String updateCard(@PathVariable Long id,
                             @RequestParam("condition") String condition,
                             @RequestParam("language") String language,
                             @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
                             @RequestParam("quantity") Integer quantity,
                             @RequestParam(value = "purchasePrice", required = false) BigDecimal purchasePrice,
                             @RequestParam(value = "sellingPrice", required = false) BigDecimal sellingPrice,
                             @RequestParam(name = "page", defaultValue = "0") int page,
                             @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                             @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                             Model model,
                             HttpServletResponse response) {

        response.setHeader("HX-Trigger", "updateStats");
        collectionService.updateCard(id, condition, language, isFoil, quantity, purchasePrice, sellingPrice);

        return filterCollection(page, sortBy, direction, "", "", "all", null, null, model);
    }
}
