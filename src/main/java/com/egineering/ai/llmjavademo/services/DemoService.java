package com.egineering.ai.llmjavademo.services;

import com.egineering.ai.llmjavademo.agents.BasicAgent;
import com.egineering.ai.llmjavademo.agents.DataAgent;
import com.egineering.ai.llmjavademo.agents.DocsAgent;
import com.egineering.ai.llmjavademo.agents.FaqAgent;
import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.models.Faq;
import com.egineering.ai.llmjavademo.repositories.FaqRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DemoService {

    @Value("classpath:/prompts/faqSystemMessage.st")
    private String faqSystemMessage;

    @Value("classpath:/prompts/docsSystemMessage.st")
    private String docsSystemMessage;

    private final BasicAgent basicAgent;
    private final FaqAgent faqAgent;
    private final FaqRepository faqRepository;
    private final DocsAgent docsAgent;
    private final DataAgent dataAgent;

    public DemoService(BasicAgent basicAgent, FaqAgent faqAgent, FaqRepository faqRepository, DocsAgent docsAgent, DataAgent dataAgent) {
        this.basicAgent = basicAgent;
        this.faqAgent = faqAgent;
        this.faqRepository = faqRepository;
        this.docsAgent = docsAgent;
        this.dataAgent = dataAgent;
    }

    public LlmResponse generateBasic(MessageForm form) {
        return new LlmResponse(basicAgent.generate(form.message()));
    }

    public LlmResponse generateFaq(MessageForm form) {

        List<Faq> faqList = faqRepository.findAll();
        String faqs = faqList.stream()
                .map(faq -> "Question: " + faq.question() + " | Answer: " + faq.answer())
                .collect(Collectors.joining("\n"));

        return new LlmResponse(faqAgent.generate(form.message(), faqs));
    }

    public LlmResponse generateDocs(MessageForm form) {

        return new LlmResponse(docsAgent.generate(form.message()));
    }

    public LlmResponse generateData(MessageForm form) {

        return new LlmResponse(dataAgent.generate(form.message()));
    }
}
