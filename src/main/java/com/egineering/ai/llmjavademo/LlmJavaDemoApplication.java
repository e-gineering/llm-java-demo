package com.egineering.ai.llmjavademo;

import com.egineering.ai.llmjavademo.configurations.LiquibaseConfiguration;
import liquibase.Liquibase;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.helpers.DbUrlConnectionCommandStep;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.ContentFormatTransformer;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.experimental.ai.chroma.ChromaApi;
import org.springframework.experimental.ai.vectorsore.ChromaVectorStore;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableMongoRepositories
public class LlmJavaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LlmJavaDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner loadDocuments(ChromaApi chromaApi, VectorStore vectorStore) {
        return args -> {
            System.out.println("### Loading documents");
            File fileResource = ResourceUtils.getFile("classpath:documents");

            if (fileResource.exists() && fileResource.isDirectory()) {
                File[] files = fileResource.listFiles();
                List<Document> documents;
                if (files != null) {
                    documents = Arrays.stream(files)
                            .flatMap(file -> {
                                PagePdfDocumentReader pdfDocumentReader =
                                        new PagePdfDocumentReader(new FileSystemResource(file.getPath()));
                                return pdfDocumentReader.get().stream();
                            })
                            .flatMap(document -> {
                                ContentFormatTransformer
                                TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
                                tokenTextSplitter.
                            })
                            .toList();
                    if (chromaApi.countEmbeddings(chromaApi.getCollection(ChromaVectorStore.DEFAULT_COLLECTION_NAME).id()) <= 0) {
                        vectorStore.add(documents);
                    }
                }
            }
            System.out.println("### Done");
        };
    }

	@Bean
    public Liquibase liquibase(LiquibaseConfiguration configuration) {
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
