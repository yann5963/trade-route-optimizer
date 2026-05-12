package com.example.mtg.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/ask")
    public String askAi(@RequestParam("query") String query, Model model, jakarta.servlet.http.HttpServletRequest request) {

        String aiResponse = chatClient.prompt()
                .user(query)
                .functions("searchBestOffers")
                .call()
                .content();

        model.addAttribute("query", query);
        model.addAttribute("response", aiResponse);

        if ("true".equals(request.getHeader("HX-Request"))) {
            return "chat-response :: chatResponse";
        }
        return "chat-response";
    }
}
