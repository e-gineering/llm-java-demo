package com.egineering.ai.llmjavademo.agents;

import com.egineering.ai.llmjavademo.configurations.LlmConfiguration;
import com.egineering.ai.llmjavademo.dtos.LlmResponse;
import com.egineering.ai.llmjavademo.dtos.MessageForm;
import com.egineering.ai.llmjavademo.dtos.StreamingLlmResponse;
import com.egineering.ai.llmjavademo.models.chromadbapi.Collection;
import com.egineering.ai.llmjavademo.models.chromadbapi.QueryRequest;
import com.egineering.ai.llmjavademo.models.chromadbapi.QueryResponse;
import com.egineering.ai.llmjavademo.services.ChromaClient;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class DocumentAgent {

    @Value("classpath:prompts/docsSystemMessage.st")
    private Resource docsSystemMessage;

    private final SimpMessagingTemplate messagingTemplate;
    private final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
    private final ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
    private final StreamingChatLanguageModel streamingChatModel;
    private final ChromaClient chromaClient;
    private final String collectionId;

    public DocumentAgent(LlmConfiguration configuration, SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
        streamingChatModel = OpenAiStreamingChatModel.withApiKey(configuration.apiKey());
        this.chromaClient = new ChromaClient("http://localhost:8000", Duration.of(5, ChronoUnit.SECONDS));
        Collection collection = chromaClient.collection("documents");
        this.collectionId = collection.getId();
    }

    @SneakyThrows
    public StreamingLlmResponse generate(MessageForm form) {

        String prompt;
        try {
            prompt = docsSystemMessage.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException ioe) {
            prompt = form.message();
        }

        Embedding questionEmbedding = embeddingModel.embed(form.message()).content();

        QueryRequest queryRequest = new QueryRequest(questionEmbedding.vectorAsList(), 10);

        QueryResponse queryResponse = chromaClient.queryCollection(this.collectionId, queryRequest);

        List<EmbeddingMatch<TextSegment>> matches = toEmbeddingMatches(queryResponse);

        String documents = matches.stream()
                .map(textSegmentEmbeddingMatch -> textSegmentEmbeddingMatch.embedded().text())
                .collect(Collectors.joining("\n"));

        SystemMessage systemMessage = PromptTemplate.from(prompt)
                .apply(Map.of("documents", documents))
                .toSystemMessage();

        chatMemory.add(systemMessage);
        chatMemory.add(UserMessage.from(form.message()));

        CompletableFuture<AiMessage> futureAiMessage = new CompletableFuture<>();

        StreamingResponseHandler<AiMessage> handler = new StreamingResponseHandler<>() {
            @Override
            public void onNext(String token) {
                messagingTemplate.convertAndSend("/topic/llmStreamingResponse", new LlmResponse(token));
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                futureAiMessage.complete(response.content());
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        };

        streamingChatModel.generate(chatMemory.messages(), handler);
        chatMemory.add(futureAiMessage.get());

        Set<String> files = queryResponse.metadatas().get(0).stream()
                .map(map -> map.get("file_name"))
                .collect(Collectors.toSet());

        return new StreamingLlmResponse(chatMemory.messages(), files);
    }

    private static List<EmbeddingMatch<TextSegment>> toEmbeddingMatches(QueryResponse queryResponse) {
        List<EmbeddingMatch<TextSegment>> embeddingMatches = new ArrayList<>();

        for(int i = 0; i < queryResponse.ids().get(0).size(); ++i) {
            double score = distanceToScore((Double)((List<?>)queryResponse.distances().get(0)).get(i));
            String embeddingId = (String)((List<?>)queryResponse.ids().get(0)).get(i);
            Embedding embedding = Embedding.from((List)((List<?>)queryResponse.embeddings().get(0)).get(i));
            TextSegment textSegment = toTextSegment(queryResponse, i);
            embeddingMatches.add(new EmbeddingMatch<>(score, embeddingId, embedding, textSegment));
        }

        return embeddingMatches;
    }

    private static double distanceToScore(double distance) {
        return 1.0 - distance / 2.0;
    }

    private static TextSegment toTextSegment(QueryResponse queryResponse, int i) {
        String text = (String)((List<?>)queryResponse.documents().get(0)).get(i);
        Map<String, String> metadata = (Map)((List<?>)queryResponse.metadatas().get(0)).get(i);
        return text == null ? null : TextSegment.from(text, metadata == null ? new Metadata() : new Metadata(metadata));
    }
}