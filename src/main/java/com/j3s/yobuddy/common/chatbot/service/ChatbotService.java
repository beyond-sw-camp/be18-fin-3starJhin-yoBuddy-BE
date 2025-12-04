package com.j3s.yobuddy.common.chatbot.service;

import com.j3s.yobuddy.common.chatbot.dto.ChatbotAskRequest;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotInternalResponse;
import com.j3s.yobuddy.common.chatbot.dto.ChatbotResponse;

public interface ChatbotService {
    ChatbotResponse ask(ChatbotAskRequest request);
}