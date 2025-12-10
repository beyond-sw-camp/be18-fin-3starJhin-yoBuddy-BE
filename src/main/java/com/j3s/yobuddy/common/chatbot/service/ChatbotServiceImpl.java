package com.j3s.yobuddy.common.chatbot.service;

import com.j3s.yobuddy.common.chatbot.dto.ChatbotAskRequest;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotInternalResponse;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${chatbot.base-url}")
    private String chatbotBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatbotResponse ask(ChatbotAskRequest request) {
        log.info(">>> [TEST] ChatbotServiceImpl.ask() called. question={}", request.getQuestion());

        return new ChatbotResponse("테스트 응답입니다. 백엔드는 정상적으로 응답하고 있어요!");
    }
}
