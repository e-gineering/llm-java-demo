package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.models.Faq;
import com.egineering.ai.llmjavademo.repositories.FaqRepository;
import org.springframework.ai.client.AiClient;
import org.springframework.ai.client.AiResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.memory.Memory;
import org.springframework.ai.openai.client.OpenAiClient;
import org.springframework.ai.prompt.Prompt;
import org.springframework.ai.prompt.SystemPromptTemplate;
import org.springframework.ai.prompt.messages.Message;
import org.springframework.ai.prompt.messages.UserMessage;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemoService {

    @Value("classpath:/prompts/faqSystemMessage.st")
    private Resource faqSystemMessage;

    @Value("classpath:/prompts/docsSystemMessage.st")
    private Resource docsSystemMessage;

    private final AiClient aiClient;
    private final FaqRepository faqRepository;
    private final VectorStore vectorStore;


    public DemoService(AiClient aiClient, FaqRepository faqRepository, VectorStore vectorStore) {
        this.aiClient = aiClient;
        this.faqRepository = faqRepository;
        this.vectorStore = vectorStore;
    }

    public LlmResponse generateBasic(MessageForm form) {
        return new LlmResponse(aiClient.generate(form.message()));
    }

    public LlmResponse generateFaq(MessageForm form) {

        List<Faq> faqList = faqRepository.findAll();
        String faqs = faqList.stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .collect(Collectors.joining("\n"));

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(faqSystemMessage);
        systemPromptTemplate.add("faqs", faqs);

        Message systemMessage = systemPromptTemplate.createMessage();
        UserMessage userMessage = new UserMessage(form.message());

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        AiResponse response = aiClient.generate(prompt);

        return new LlmResponse(response.getGeneration().getText());
    }

    public LlmResponse generateDocs(MessageForm form) {

        List<Document> documents = vectorStore.similaritySearch(form.message());

        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(docsSystemMessage);
        systemPromptTemplate.add("documents", documents);

        Message systemMessage = systemPromptTemplate.createMessage();
        UserMessage userMessage = new UserMessage(form.message());

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        AiResponse response = aiClient.generate(prompt);

        return new LlmResponse(response.getGeneration().getText());
    }
}
