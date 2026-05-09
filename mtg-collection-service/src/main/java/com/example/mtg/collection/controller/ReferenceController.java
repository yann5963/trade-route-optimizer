package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.MtgCardReference;
import com.example.mtg.collection.entity.MtgSet;
import com.example.mtg.collection.entity.SyncStatus;
import com.example.mtg.collection.repository.MtgCardReferenceRepository;
import com.example.mtg.collection.repository.MtgSetRepository;
import com.example.mtg.collection.repository.SyncStatusRepository;
import com.example.mtg.collection.service.ScryfallSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ReferenceController {

    private final MtgSetRepository setRepository;
    private final MtgCardReferenceRepository cardReferenceRepository;
    private final SyncStatusRepository syncStatusRepository;
    private final ScryfallSyncService scryfallSyncService;

    public ReferenceController(MtgSetRepository setRepository,
                               MtgCardReferenceRepository cardReferenceRepository,
                               SyncStatusRepository syncStatusRepository,
                               ScryfallSyncService scryfallSyncService) {
        this.setRepository = setRepository;
        this.cardReferenceRepository = cardReferenceRepository;
        this.syncStatusRepository = syncStatusRepository;
        this.scryfallSyncService = scryfallSyncService;
    }

    @GetMapping("/reference/sets")
    @ResponseBody
    public List<MtgSet> getSets() {
        return setRepository.findAll();
    }

    @GetMapping("/reference/cards")
    public String getCardSuggestions(@RequestParam(name = "setName", required = false) String setCode,
                                     @RequestParam(name = "name", required = false, defaultValue = "") String query,
                                     Model model) {
        List<MtgCardReference> cards;
        if (setCode == null || setCode.isEmpty()) {
            cards = List.of();
        } else {
            cards = cardReferenceRepository.findBySetCodeAndNameContainingIgnoreCase(setCode, query);
            // Limit suggestions to prevent huge payload
            cards = cards.stream().limit(50).collect(Collectors.toList());
        }

        model.addAttribute("suggestions", cards);
        return "fragments/card-suggestions :: datalist-options";
    }

    @PostMapping("/admin/sync-scryfall")
    public String syncScryfall(Model model) {
        scryfallSyncService.syncAllReferences();

        SyncStatus status = syncStatusRepository.findTopByOrderByIdDesc().orElse(new SyncStatus());
        model.addAttribute("syncStatus", status);

        return "views/settings :: sync-status-fragment";
    }
}
