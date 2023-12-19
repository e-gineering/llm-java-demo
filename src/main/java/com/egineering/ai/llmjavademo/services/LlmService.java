package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.configurations.LlmConfiguration;
import org.springframework.stereotype.Service;

@Service
public class LlmService {

    private final LlmConfiguration configuration;

    public LlmService(LlmConfiguration configuration1) {
        this.configuration = configuration1;
    }


}
