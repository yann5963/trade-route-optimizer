package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.Card;
import com.example.mtg.collection.entity.WishlistCard;
import com.example.mtg.collection.repository.CardRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.WishlistCardRepository;
import com.example.mtg.collection.service.CollectionService;
import com.example.mtg.collection.service.WishlistService;
import com.example.mtg.collection.specification.CardSpecifications;
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
import java.time.LocalDate;

@Controller
@RequestMapping("/ui/wishlist")
@RequiredArgsConstructor
public class WishlistUIController {

    @Value("${app.pagination.max-record-wishlist:6}")
    private int maxRecordWishlist;

    private final WishlistCardRepository wishlistCardRepository;
    private final MtgSetRepository mtgSetRepository;
    private final CardRepository cardRepository;
    private final WishlistService wishlistService;
    private final CollectionService collectionService;

    @GetMapping
    public String getWishlistDashboard(@RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                       @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                       Model model) {
        PageRequest pageRequest = PageRequest.of(page, maxRecordWishlist, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<WishlistCard> wishlistCardPage = wishlistCardRepository.findAll(pageRequest);

        model.addAttribute("wishlistCards", wishlistCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "views/wishlist";
    }

    @GetMapping("/filter")
    public String filterWishlist(@RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                 @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                 @RequestParam(name = "filter", required = false, defaultValue = "") String filter,
                                 @RequestParam(name = "setCode", required = false, defaultValue = "") String setCode,
                                 @RequestParam(name = "foil", required = false, defaultValue = "all") String foil,
                                 @RequestParam(name = "minPrice", required = false) BigDecimal minPrice,
                                 @RequestParam(name = "maxPrice", required = false) BigDecimal maxPrice,
                                 Model model) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        PageRequest pageRequest = PageRequest.of(page, maxRecordWishlist, sort);

        Specification<WishlistCard> spec = CardSpecifications.filterBy(filter, setCode, foil, minPrice, maxPrice, "maxPrice");

        Page<WishlistCard> wishlistCardPage = wishlistCardRepository.findAll(spec, pageRequest);
        model.addAttribute("wishlistCards", wishlistCardPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", wishlistCardPage.getTotalPages());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        return "views/wishlist :: wishlistTable";
    }

    @GetMapping("/add")
    public String getAddWishlistCardForm(Model model) {
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "fragments/add-wishlist-modal :: add-wishlist-form";
    }

    @PostMapping("/add")
    public String addWishlistCard(@RequestParam("name") String name,
                                  @RequestParam("setName") String setName,
                                  @RequestParam(value = "rarity", required = false, defaultValue = "Common") String rarityParam,
                                  @RequestParam("condition") String condition,
                                  @RequestParam("language") String language,
                                  @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
                                  @RequestParam("quantity") Integer quantity,
                                  @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                  @RequestParam(name = "page", defaultValue = "0") int page,
                                  @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                  @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                  @RequestParam(value = "marketPrice", required = false) BigDecimal marketPrice,
                                  @RequestParam(value = "marketPriceDate", required = false) LocalDate marketPriceDate,
                                  Model model) {

        String resolvedSetCode = collectionService.resolveSetCode(setName);

        Card card = cardRepository.findByNameIgnoreCaseAndSetNameIgnoreCase(name, resolvedSetCode)
                .map(existingCard -> collectionService.fixCardRarityIfNeeded(existingCard, name, resolvedSetCode))
                .orElseGet(() -> {
                    String rarity = collectionService.resolveRarity(name, resolvedSetCode, rarityParam);
                    return cardRepository.save(new Card(name, resolvedSetCode, rarity));
                });

        wishlistService.addWishlistCard(card, condition, language, isFoil, quantity, maxPrice, marketPrice, marketPriceDate);

        return filterWishlist(page, sortBy, direction, "", "", "all", null, null, model);
    }

    @GetMapping("/edit/{id}")
    public String getEditWishlistCardForm(@PathVariable Long id, Model model) {
        wishlistCardRepository.findById(id).ifPresent(wishCard -> model.addAttribute("wishCard", wishCard));
        model.addAttribute("mtgSets", mtgSetRepository.findAll(Sort.by(Sort.Direction.ASC, "name")));
        return "fragments/edit-wishlist-modal :: edit-wishlist-form";
    }

    @PostMapping("/edit/{id}")
    public String updateWishlistCard(@PathVariable Long id,
                                     @RequestParam("condition") String condition,
                                     @RequestParam("language") String language,
                                     @RequestParam(value = "isFoil", required = false, defaultValue = "false") Boolean isFoil,
                                     @RequestParam("quantity") Integer quantity,
                                     @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                                     @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "sortBy", defaultValue = "card.name") String sortBy,
                                     @RequestParam(name = "direction", defaultValue = "ASC") String direction,
                                     @RequestParam(value = "marketPrice", required = false) BigDecimal marketPrice,
                                     @RequestParam(value = "marketPriceDate", required = false) LocalDate marketPriceDate,
                                     Model model) {
        wishlistService.updateWishlistCard(id, condition, language, isFoil, quantity, maxPrice, marketPrice, marketPriceDate);
        return filterWishlist(page, sortBy, direction, "", "", "all", null, null, model);
    }
}
