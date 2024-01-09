package com.egineering.ai.llmjavademo.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "langchain4j.open-ai.chat-model")
public record LlmConfiguration (String apiKey, String modelName) {}
