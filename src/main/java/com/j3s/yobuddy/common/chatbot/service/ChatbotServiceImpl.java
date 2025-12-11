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
                // 응답이 비정상일 때는 기존 서버 오류 문구 사용
                return new ChatbotResponse("죄송합니다. 챗봇 서버 응답이 올바르지 않습니다.");
            }

            // 정상 응답
            return new ChatbotResponse(body.getAnswer());

        } catch (HttpStatusCodeException e) {
            int statusCode = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();

            log.error("Chatbot server returned error. status={}, body={}", statusCode, responseBody, e);

            if (statusCode == 500) {
                return new ChatbotResponse(
                    "현재 분당 사용량 제한을 초과했습니다.\n잠시 뒤에 다시 시도해 주세요."
                );
            }

            return new ChatbotResponse(
                "죄송합니다. 챗봇 서버 호출 중 오류가 발생했습니다."
            );

        } catch (Exception e) {
            log.error("Error while calling chatbot server", e);
            return new ChatbotResponse(
                "죄송합니다. 챗봇 서버 호출 중 오류가 발생했습니다ㅠㅠ."
            );
        }
    }
}
