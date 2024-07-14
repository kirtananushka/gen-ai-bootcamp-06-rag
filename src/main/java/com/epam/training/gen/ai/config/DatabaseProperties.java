package com.epam.training.gen.ai.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.datasource")
public record DatabaseProperties(
        String url,
        String username,
        String password,
        int port,
        String database,
        String table,
        int dimension
) {
}
