package com.egineering.ai.llmjavademo.dtos;

import java.util.Set;

public record StreamingLlmResponse(String prompt, Set<String> files) {}
