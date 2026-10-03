package com.careerconnect.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String testAi() {
        return chatClient.prompt()
                .user("Say hello to Career Connect in one sentence.")
                .call()
                .content();
    }
}