package com.lucian.urlshortener.utility;

import com.lucian.urlshortener.dto.UrlResponse;
import com.lucian.urlshortener.entity.UrlMapping;

public class Mapper {
  private Mapper() {
    // Default constructor
  }

  public static UrlResponse toUrlResponse(UrlMapping mapping) {
    return UrlResponse.builder()
        .alias(mapping.getAlias())
        .fullUrl(mapping.getFullUrl())
        .shortUrl(mapping.getShortUrl())
        .build();
  }
}
