package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface WsAgent {

    TokenStream generate(@UserMessage String userMessage);
}
