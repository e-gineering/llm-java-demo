package com.egineering.ai.llmjavademo.agents;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface FaqAgent {

    @SystemMessage("""
            You are an assistant answering questions about a structural steel company called Brown Strauss.
            Use the information contained in the frequently asked questions in the FAQs section below.
            If unsure, simply state that you don't know.
            
            FAQs:
            {{faqs}}""")
    TokenStream generate(@UserMessage String userMessage, @V("faqs") String faqs);
}
