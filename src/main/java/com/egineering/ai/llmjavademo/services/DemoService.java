package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.BasicStreamingAgent;
import com.egineering.ai.llmjavademo.agents.DataAgent;
import com.egineering.ai.llmjavademo.agents.DocumentStreamingAgent;
import com.egineering.ai.llmjavademo.agents.FaqStreamingAgent;
import com.egineering.ai.llmjavademo.agents.SqlAgent;
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
    private final ChatMemory sqlChatMemory;
    private final SqlAgent sqlAgent;
    private final ChatMemory dataChatMemory;
    private final DataAgent dataAgent;

    public DemoService(SimpMessagingTemplate messagingTemplate, BasicStreamingAgent basicStreamingAgent,
                       FaqStreamingAgent faqStreamingAgent, FaqRepository faqRepository,
                       DocumentStreamingAgent documentStreamingAgent, ChatMemory sqlChatMemory,
                       SqlAgent sqlAgent, ChatMemory dataChatMemory, DataAgent dataAgent) {
        this.messagingTemplate = messagingTemplate;
        this.basicStreamingAgent = basicStreamingAgent;
        this.faqStreamingAgent = faqStreamingAgent;
        this.faqRepository = faqRepository;
        this.documentStreamingAgent = documentStreamingAgent;
        this.sqlChatMemory = sqlChatMemory;
        this.sqlAgent = sqlAgent;
        this.dataChatMemory = dataChatMemory;
        this.dataAgent = dataAgent;
    }

    public StreamingLlmResponse generateBasic(MessageForm form) {
        return basicStreamingAgent.generate(form);
    }

    public void resetBasic(boolean doReset) {
        if (doReset) basicStreamingAgent.reset();
    }

    public StreamingLlmResponse generateFaq(MessageForm form) {

        List<String> documents = faqRepository.findAll().stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .toList();

        return faqStreamingAgent.generate(form, documents);
    }

    public void resetFaq(boolean doReset) {
        if (doReset) faqStreamingAgent.reset();
    }

    public StreamingLlmResponse generateDocuments(MessageForm form) {
        return documentStreamingAgent.generate(form);
    }

    public void resetDocuments(boolean doReset) {
        if (doReset) documentStreamingAgent.reset();
    }

    public StreamingLlmResponse generateData(MessageForm form) {

        dataAgent.generate(form.message())
                .onNext(token -> messagingTemplate.convertAndSend("/topic/data/llmStreamingResponse", new LlmResponse(token)))
                .onError(Throwable::printStackTrace)
                .start();

        sqlChatMemory.clear();
        return new StreamingLlmResponse(dataChatMemory.messages(), Collections.emptyList(), Collections.emptySet());
    }

    public void resetData(boolean doReset) {
        if (doReset) dataChatMemory.clear();
    }

}
