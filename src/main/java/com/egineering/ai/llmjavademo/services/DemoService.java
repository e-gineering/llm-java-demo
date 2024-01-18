package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.BasicStreamingAgent;
import com.egineering.ai.llmjavademo.agents.DataAgent;
import com.egineering.ai.llmjavademo.agents.DocumentStreamingAgent;
import com.egineering.ai.llmjavademo.agents.FaqStreamingAgent;
import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.dtos.StreamingLlmResponse;
import com.egineering.ai.llmjavademo.repositories.FaqRepository;
import dev.langchain4j.memory.ChatMemory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DemoService {

    private final SimpMessagingTemplate messagingTemplate;
    private final BasicStreamingAgent basicStreamingAgent;
    private final FaqStreamingAgent faqStreamingAgent;
    private final FaqRepository faqRepository;
    private final DocumentStreamingAgent documentStreamingAgent;
    private final ChatMemory dataChatMemory;
    private final DataAgent dataAgent;

    public DemoService(SimpMessagingTemplate messagingTemplate, BasicStreamingAgent basicStreamingAgent,
                       FaqStreamingAgent faqStreamingAgent, FaqRepository faqRepository,
                       DocumentStreamingAgent documentStreamingAgent, ChatMemory dataChatMemory, DataAgent dataAgent) {
        this.messagingTemplate = messagingTemplate;
        this.basicStreamingAgent = basicStreamingAgent;
        this.faqStreamingAgent = faqStreamingAgent;
        this.faqRepository = faqRepository;
        this.documentStreamingAgent = documentStreamingAgent;
        this.dataChatMemory = dataChatMemory;
        this.dataAgent = dataAgent;
    }

    public StreamingLlmResponse generateBasic(MessageForm form) {
        return basicStreamingAgent.generate(form);
    }

    public StreamingLlmResponse generateFaq(MessageForm form) {

        List<String> documents = faqRepository.findAll().stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .toList();

        return faqStreamingAgent.generate(form, documents);
    }

    public StreamingLlmResponse generateDocuments(MessageForm form) {
        return documentStreamingAgent.generate(form);
    }

    public StreamingLlmResponse generateData(MessageForm form) {

        dataAgent.generate(form.message())
                .onNext(token -> messagingTemplate.convertAndSend("/topic/data/llmStreamingResponse", new LlmResponse(token)))
                .onError(Throwable::printStackTrace)
                .start();

        return new StreamingLlmResponse(dataChatMemory.messages(), Collections.emptyList(), Collections.emptySet());
    }
}
