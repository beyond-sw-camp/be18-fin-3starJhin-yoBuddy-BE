package com.j3s.yobuddy.api.common;

import com.j3s.yobuddy.common.chatbot.dto.ChatbotAskRequest;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotResponse;
import com.j3s.yobuddy.common.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ChatbotResponse ask(@RequestBody ChatbotAskRequest request) {
        return chatbotService.ask(request);
    }
}
