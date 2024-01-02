package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface DocsAgent {

    @SystemMessage("""
            You are an assistant answering questions about structural steel.
            If unsure, simply state that you don't know.""")
    String generate(@UserMessage String userMessage);
}
