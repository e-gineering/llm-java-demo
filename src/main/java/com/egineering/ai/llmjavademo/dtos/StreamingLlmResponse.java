package com.egineering.ai.llmjavademo.dtos;

import dev.langchain4j.data.message.ChatMessage;

import java.util.List;
import java.util.Set;

public record StreamingLlmResponse(List<ChatMessage> messages, Set<String> files) {}
