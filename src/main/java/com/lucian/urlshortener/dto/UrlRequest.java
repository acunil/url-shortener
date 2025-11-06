package com.lucian.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record UrlRequest(@NotBlank @URL String fullUrl, String customAlias) {}
