package com.lucian.urlshortener.service;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UrlShortenerService {

  private final UrlMappingRepository urlMappingRepository;

  public UrlMapping createShortUrl(String fullUrl, Optional<String> alias) {
    // validate, check for alias duplication, generate shortUrl
    String shortUrl = "https://short.url"; // placeholder
    return UrlMapping.builder()
        .shortUrl(shortUrl)
        .fullUrl(fullUrl)
        .alias(alias.orElse(null))
        .build();
  }

  public UrlMapping getByAlias(String alias) {
    // throw 404 if not found
    return null;
  }

  public void deleteByAlias(String alias) {
    // throw 404 if not found
  }

  public List<UrlMapping> listAll() {
    return null;
  }
}
