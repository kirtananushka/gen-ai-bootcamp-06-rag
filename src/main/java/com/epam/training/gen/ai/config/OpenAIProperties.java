package com.epam.training.gen.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.azure.openai")
public record OpenAIProperties(
        String apiKey,
        String endpoint
) {
}
