package com.epam.training.gen.ai.model;

import jakarta.validation.constraints.NotEmpty;

public record ChatRequest(
        @NotEmpty(message = "Query cannot be empty") String query) {
}