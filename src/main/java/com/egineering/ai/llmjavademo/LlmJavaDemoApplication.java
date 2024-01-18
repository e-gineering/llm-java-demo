package com.egineering.ai.llmjavademo;

import com.egineering.ai.llmjavademo.agents.DataAgent;
import com.egineering.ai.llmjavademo.agents.SqlAgent;
import com.egineering.ai.llmjavademo.configurations.LiquibaseConfiguration;
import com.egineering.ai.llmjavademo.configurations.LlmConfiguration;
import com.egineering.ai.llmjavademo.models.chromadbapi.Collection;
import com.egineering.ai.llmjavademo.serializers.ChatMessageSerializer;
import com.egineering.ai.llmjavademo.services.SqlRetriever;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.retriever.EmbeddingStoreRetriever;
import dev.langchain4j.retriever.Retriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
public class LlmJavaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmJavaDemoApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.serializerByType(ChatMessage.class, new ChatMessageSerializer());
    }

    @Bean
    public StreamingChatLanguageModel streamingChatModel(LlmConfiguration configuration) {
        return OllamaStreamingChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("llama2")
                .temperature(0.0)
                .timeout(Duration.of(1, ChronoUnit.MINUTES))
                .build();
//        return OpenAiStreamingChatModel.withApiKey(configuration.apiKey());
    }

    @Bean
    public SqlAgent sqlAgent(ChatLanguageModel chatLanguageModel) {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("codelamma")
                .temperature(0.0)
                .timeout(Duration.of(1, ChronoUnit.MINUTES))
                .build();
        return AiServices.builder(SqlAgent.class)
                .chatLanguageModel(model)
                .build();
    }

    @Bean
    public ChatMemory dataChatMemory() {
        return MessageWindowChatMemory.withMaxMessages(20);
    }

    @Bean
    public DataAgent dataAgent(StreamingChatLanguageModel streamingChatModel, ChatMemory dataChatMemory, SqlRetriever sqlRetriever) {
        return AiServices.builder(DataAgent.class)
                .streamingChatLanguageModel(streamingChatModel)
                .chatMemory(dataChatMemory)
                .retriever(sqlRetriever)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder()
                .defaultMessageConverters()
                .rootUri("http://localhost:8000/api/v1")
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(EmbeddingModel embeddingModel, RestTemplate restTemplate) throws IOException {

        EmbeddingStore<TextSegment> embeddingStore = ChromaEmbeddingStore.builder()
                .baseUrl("http://localhost:8000")
                .collectionName("documents")
                .build();

        Collection collection = restTemplate.getForObject("/collections/documents", Collection.class);

        if (collection != null) {

            String collectionCountStr = restTemplate.getForObject("/collections/" + collection.getId() + "/count", String.class);

            if (collectionCountStr != null && Integer.parseInt(collectionCountStr) <= 0) {

                File fileResource = ResourceUtils.getFile("classpath:documents");
                if (fileResource.exists() && fileResource.isDirectory()) {

                    System.out.println("### Loading documents");

                    Tokenizer tokenizer = new OpenAiTokenizer(GPT_3_5_TURBO);
                    DocumentSplitter documentSplitter = DocumentSplitters.recursive(120, 10, tokenizer);
                    EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                            .documentSplitter(documentSplitter)
                            .embeddingModel(embeddingModel)
                            .embeddingStore(embeddingStore)
                            .build();

                    FileSystemDocumentLoader.loadDocuments(Path.of(fileResource.toURI()), new ApachePdfBoxDocumentParser())
                            .forEach(ingestor::ingest);

                    System.out.println("### Done");
                }
            }
        }

        return embeddingStore;
    }

    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    public Retriever<TextSegment> retriever(EmbeddingStore<TextSegment> embeddingStore, EmbeddingModel embeddingModel) {
        int maxResultsRetrieved = 10;
        double minScore = 0.6;

        return EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, maxResultsRetrieved, minScore);
    }

    @Bean
    public SqlRetriever sqlRetriever(JdbcTemplate jdbcTemplate, SqlAgent sqlAgent) {
        return new SqlRetriever(jdbcTemplate, sqlAgent);
    }

    @Bean
    public Liquibase mongodbLiquibase(LiquibaseConfiguration configuration) {
        if (configuration.enabled()) {
            try (MongoLiquibaseDatabase database = (MongoLiquibaseDatabase) DatabaseFactory.getInstance().openDatabase(
                    configuration.url(), configuration.user(), configuration.password(), null,
                    new ClassLoaderResourceAccessor());
                 Liquibase liquibase = new Liquibase(configuration.changeLog(), new ClassLoaderResourceAccessor(), database)) {

                CommandScope updateCommand = new CommandScope(UpdateCommandStep.COMMAND_NAME);
                updateCommand.addArgumentValue(DbUrlConnectionCommandStep.DATABASE_ARG, database);
                updateCommand.addArgumentValue(UpdateCommandStep.CHANGELOG_FILE_ARG, configuration.changeLog());
                updateCommand.execute();

                return liquibase;
            } catch (LiquibaseException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
