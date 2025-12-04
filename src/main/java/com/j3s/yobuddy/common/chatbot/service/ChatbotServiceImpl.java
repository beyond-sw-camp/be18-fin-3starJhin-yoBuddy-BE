package com.j3s.yobuddy.common.chatbot.service;

import com.j3s.yobuddy.common.chatbot.dto.ChatbotAskRequest;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotInternalResponse;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    @Value("${chatbot.base-url}")
    private String chatbotBaseUrl;   // http://localhost:8000

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatbotResponse ask(ChatbotAskRequest request) {
        try {
            String url = chatbotBaseUrl + "/api/faq/ask";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<ChatbotAskRequest> entity = new HttpEntity<>(request, headers);

            log.info("Calling chatbot server: {}", url);
            ResponseEntity<ChatbotInternalResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, ChatbotInternalResponse.class);

            ChatbotInternalResponse body = response.getBody();
            log.info("Chatbot raw response: status={}, body={}", response.getStatusCode(), body);

            if (body == null || body.getAnswer() == null) {
                return new ChatbotResponse("죄송합니다. 챗봇 서버 응답이 올바르지 않습니다.");
            }

            return new ChatbotResponse(body.getAnswer());

        } catch (Exception e) {
            // ★ 여기서 예외를 '먹고' 항상 ChatbotResponse를 리턴해야
            //    프론트에서 500이 아니라 200으로 처리됨
            log.error("Error while calling chatbot server", e);
            return new ChatbotResponse("죄송합니다. 챗봇 서버 호출 중 오류가 발생했습니다.");
        }
    }
}
