package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BasicStreamingAgent extends AbstractStreamingAgent {

    protected BasicStreamingAgent(StreamingChatLanguageModel streamingChatModel, SimpMessagingTemplate messagingTemplate) {
        super(streamingChatModel, messagingTemplate, "/topic/basic/llmStreamingResponse");
    }
}
