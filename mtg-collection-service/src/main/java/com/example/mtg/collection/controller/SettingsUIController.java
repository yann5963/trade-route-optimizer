package com.example.mtg.collection.controller;

import com.example.mtg.collection.entity.SyncStatus;
import com.example.mtg.collection.repository.SyncStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/ui/settings")
@RequiredArgsConstructor
public class SettingsUIController {

    private final SyncStatusRepository syncStatusRepository;

    @GetMapping
    public String getSettingsPage(Model model) {
        SyncStatus status = syncStatusRepository.findTopByOrderByIdDesc().orElse(null);
        model.addAttribute("syncStatus", status);
        return "views/settings";
    }
}
