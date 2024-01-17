package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.BasicAgent;
import com.egineering.ai.llmjavademo.agents.DataAgent;
import com.egineering.ai.llmjavademo.agents.FaqAgent;
import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.dtos.StreamingLlmResponse;
import com.egineering.ai.llmjavademo.models.Faq;
import com.egineering.ai.llmjavademo.repositories.FaqRepository;
import dev.langchain4j.memory.ChatMemory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DemoService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMemory basicChatMemory;
    private final BasicAgent basicAgent;
    private final ChatMemory faqChatMemory;
    private final FaqAgent faqAgent;
    private final FaqRepository faqRepository;
    private final ChatMemory dataChatMemory;
    private final DataAgent dataAgent;

    public DemoService(SimpMessagingTemplate messagingTemplate, ChatMemory basicChatMemory, BasicAgent basicAgent,
                       ChatMemory faqChatMemory, FaqAgent faqAgent, FaqRepository faqRepository,
                       ChatMemory dataChatMemory, DataAgent dataAgent) {
        this.messagingTemplate = messagingTemplate;
        this.basicChatMemory = basicChatMemory;
        this.basicAgent = basicAgent;
        this.faqChatMemory = faqChatMemory;
        this.faqAgent = faqAgent;
        this.faqRepository = faqRepository;
        this.dataChatMemory = dataChatMemory;
        this.dataAgent = dataAgent;
    }

    public StreamingLlmResponse generateBasic(MessageForm form) {

        basicAgent.generate(form.message())
                .onNext(token -> messagingTemplate.convertAndSend("/topic/basic/llmStreamingResponse", new LlmResponse(token)))
                .onError(Throwable::printStackTrace)
                .start();

        return new StreamingLlmResponse(basicChatMemory.messages(), Collections.emptySet());
    }

    public StreamingLlmResponse generateFaq(MessageForm form) {

        List<Faq> faqList = faqRepository.findAll();

        Set<String> faqSet = faqList.stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .collect(Collectors.toSet());

        String faqs = faqList.stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .collect(Collectors.joining("\n"));

        faqAgent.generate(form.message(), faqs)
                .onNext(token -> messagingTemplate.convertAndSend("/topic/faq/llmStreamingResponse", new LlmResponse(token)))
                .onError(Throwable::printStackTrace)
                .start();

        return new StreamingLlmResponse(faqChatMemory.messages(), faqSet);
    }

    public StreamingLlmResponse generateData(MessageForm form) {

        dataAgent.generate(form.message())
                .onNext(token -> messagingTemplate.convertAndSend("/topic/data/llmStreamingResponse", new LlmResponse(token)))
                .onError(Throwable::printStackTrace)
                .start();

        return new StreamingLlmResponse(dataChatMemory.messages(), Collections.emptySet());
    }
}
