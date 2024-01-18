package com.egineering.ai.llmjavademo.agents;

import com.egineering.ai.llmjavademo.repositories.FaqRepository;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FaqStreamingAgent extends AbstractStreamingAgent{

    protected FaqStreamingAgent(StreamingChatLanguageModel streamingChatModel, SimpMessagingTemplate messagingTemplate, FaqRepository faqRepository) {
        super(streamingChatModel, messagingTemplate, "/topic/faq/llmStreamingResponse", "prompts/faqSystemMessage.st");
    }
}
