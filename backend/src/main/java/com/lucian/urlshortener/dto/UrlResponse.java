package com.lucian.urlshortener.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record UrlResponse(
    @Schema(description = "The alias for the shortened URL", example = "my-custom-alias")
    String alias,

    @Schema(description = "The original full URL", example = "https://example.com/very/long/url")
    String fullUrl,

    @Schema(description = "The shortened URL", example = "http://localhost:8080/my-custom-alias")
    String shortUrl
) {}
