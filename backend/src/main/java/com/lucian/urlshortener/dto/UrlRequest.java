package com.lucian.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(
    @Schema(description = "The full URL to shorten", example = "https://example.com/very/long/url")
    @NotBlank
    @URL
    String fullUrl,

    @Schema(description = "Optional custom alias", example = "my-custom-alias")
    String customAlias
) {}
