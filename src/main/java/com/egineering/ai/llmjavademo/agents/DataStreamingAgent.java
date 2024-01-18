package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;

public class DataStreamingAgent extends AbstractStreamingAgent {

    protected DataStreamingAgent(StreamingChatLanguageModel streamingChatModel, SimpMessagingTemplate messagingTemplate) {
        super(streamingChatModel, messagingTemplate, "/topic/data/llmStreamingResponse", "prompts/dataSystemMessage.st");
    }
}
