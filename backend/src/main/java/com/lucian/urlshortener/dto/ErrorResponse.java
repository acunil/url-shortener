package com.lucian.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import lombok.Builder;

@Builder
public record ErrorResponse(
    @Schema(description = "Timestamp of the error", example = "2025-11-07T16:42:01.123Z")
        Instant timestamp,
    @Schema(description = "HTTP status code", example = "400") int status,
    @Schema(description = "Error type", example = "Bad Request") String error,
    @Schema(description = "Detailed error message", example = "Alias already exists: myAlias")
        String message,
    @Schema(description = "Request path", example = "/shorten") String path) {}
