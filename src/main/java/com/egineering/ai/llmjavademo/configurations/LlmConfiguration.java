package com.egineering.ai.llmjavademo.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "openai.api")
public record LlmConfiguration (String key) {}
