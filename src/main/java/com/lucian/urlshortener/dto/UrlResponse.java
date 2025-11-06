package com.lucian.urlshortener.dto;

import lombok.Builder;

@Builder
public record UrlResponse(String alias, String fullUrl, String shortUrl) {}
