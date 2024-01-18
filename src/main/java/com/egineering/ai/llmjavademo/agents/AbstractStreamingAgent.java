package com.egineering.ai.llmjavademo.agents;

import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.dtos.StreamingLlmResponse;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class AbstractStreamingAgent {

    protected final SimpMessagingTemplate messagingTemplate;
    protected final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    protected final StreamingChatLanguageModel streamingChatModel;
    protected String sytemMessageString;
    protected final String outputTopic;

    @SneakyThrows
    protected AbstractStreamingAgent(StreamingChatLanguageModel streamingChatModel, SimpMessagingTemplate messagingTemplate, String outputTopic, String systemMessageFile) {
        this.messagingTemplate = messagingTemplate;
        this.streamingChatModel = streamingChatModel;
        this.outputTopic = outputTopic;
        if (systemMessageFile != null) sytemMessageString = new ClassPathResource(systemMessageFile).getContentAsString(StandardCharsets.UTF_8);
    }

    protected AbstractStreamingAgent(StreamingChatLanguageModel streamingChatModel, SimpMessagingTemplate messagingTemplate, String outputTopic) {
        this(streamingChatModel, messagingTemplate, outputTopic, null);
    }

    @SneakyThrows
    public StreamingLlmResponse generate(MessageForm form, List<String> documents, Set<String> files) {

        if (StringUtils.hasLength(sytemMessageString)) {
            SystemMessage systemMessage = PromptTemplate.from(sytemMessageString)
                    .apply(Map.of("documents", String.join("/n", documents)))
                    .toSystemMessage();

            chatMemory.add(systemMessage);
        }

        chatMemory.add(UserMessage.from(form.message()));

        CompletableFuture<AiMessage> futureAiMessage = new CompletableFuture<>();

        StreamingResponseHandler<AiMessage> handler = new StreamingResponseHandler<>() {
            @Override
            public void onNext(String token) {
                messagingTemplate.convertAndSend(outputTopic, new LlmResponse(token));
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureAiMessage.complete(response.content());
            }

            @Override
            public void onError(Throwable error) {
                log.error("Error handling streaming response.", error);
            }
        };

        streamingChatModel.generate(chatMemory.messages(), handler);

        chatMemory.add(futureAiMessage.get());

        return new StreamingLlmResponse(chatMemory.messages(), documents, files);
    }

    public StreamingLlmResponse generate(MessageForm form, List<String> documents) {
        return generate(form, documents, Collections.emptySet());
    }

    public StreamingLlmResponse generate(MessageForm form) {
        return generate(form, Collections.emptyList(), Collections.emptySet());
    }

    public void reset() {
        chatMemory.clear();
    }
}
