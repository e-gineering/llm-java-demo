package com.egineering.ai.llmjavademo;

import com.egineering.ai.llmjavademo.agents.FaqAgent;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.BertTokenizer;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

public class Tests {

    @Test
    public void test() throws IOException {
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        File fileResource = ResourceUtils.getFile("classpath:jackson_lottery.pdf");
        Document document = loadDocument(fileResource.toPath(), new ApachePdfBoxDocumentParser());

        DocumentSplitter documentSplitter = DocumentSplitters.recursive(100, 2, new BertTokenizer());
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(documentSplitter)
                .embeddingModel(new AllMiniLmL6V2EmbeddingModel())
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);
    }

    @Test
    public void test2() throws NoSuchFieldException, IllegalAccessException {
        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .temperature(0.0)
                .build();

        FaqAgent faqAgent = AiServices.builder(FaqAgent.class)
                .streamingChatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
                .build();

        Field defaultAiServiceField = Proxy.getInvocationHandler(faqAgent).getClass().getDeclaredField("context");
        defaultAiServiceField.setAccessible(true);
        Object defaultAiServices = defaultAiServiceField.get(AiServices.class);
        Proxy.getInvocationHandler(faqAgent);
    }
}
