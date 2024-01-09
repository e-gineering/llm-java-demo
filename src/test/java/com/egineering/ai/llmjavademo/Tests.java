package com.egineering.ai.llmjavademo;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.BertTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

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
}
