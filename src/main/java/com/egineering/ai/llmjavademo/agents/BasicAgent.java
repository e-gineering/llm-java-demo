package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.service.TokenStream;

public interface BasicAgent {

    TokenStream generate(String userMessage);
}
