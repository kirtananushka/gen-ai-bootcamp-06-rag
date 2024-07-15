package com.epam.training.gen.ai.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.azure.AzureOpenAiChatModel;
import dev.langchain4j.model.azure.AzureOpenAiEmbeddingModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AppConfig {
    private final OpenAIProperties properties;
    private final DatabaseProperties databaseProperties;

    @Bean
    public ChatLanguageModel chatModel() {
        return AzureOpenAiChatModel.builder()
                .apiKey(properties.apiKey())
                .endpoint(properties.endpoint())
                .build();
    }

    @Bean
    AzureOpenAiEmbeddingModel embeddingModel() {
        return AzureOpenAiEmbeddingModel
                .builder()
                .apiKey(properties.apiKey())
                .endpoint(properties.endpoint())
                .logRequestsAndResponses(true)
                .build();
    }

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        return PgVectorEmbeddingStore.builder()
                .host(databaseProperties.url())
                .port(databaseProperties.port())
                .user(databaseProperties.username())
                .password(databaseProperties.password())
                .database(databaseProperties.database())
                .table(databaseProperties.table())
                .dimension(databaseProperties.dimension())
                .build();
    }

    @Bean
    public DocumentSplitter documentSplitter() {
        return DocumentSplitters.recursive(1000, 50);
    }

    @Bean
    public EmbeddingStoreIngestor ingestor() {
        return EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel())
                .embeddingStore(embeddingStore())
                .documentSplitter(documentSplitter())
                .build();
    }
}
