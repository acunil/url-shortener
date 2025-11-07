package com.lucian.urlshortener.service;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.exception.*;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import com.lucian.urlshortener.utility.AliasGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.lucian.urlshortener.utility.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class UrlShortenerService {

  private final UrlMappingRepository urlMappingRepository;
  private final AliasGenerator aliasGenerator;
  private final String baseUrl;

  public UrlShortenerService(
      UrlMappingRepository urlMappingRepository,
      AliasGenerator aliasGenerator,
      @Value("${app.base-url:http://localhost:8080}") String baseUrl) {
    this.urlMappingRepository = urlMappingRepository;
    this.aliasGenerator = aliasGenerator;
    this.baseUrl = baseUrl;
  }

  public static final String ALIAS_REGEX = "[A-Za-z0-9\\-_]{3,64}";
  private static final int MAX_GENERATION_ATTEMPTS = 5;

  @Transactional
  public UrlMapping createShortUrl(String fullUrl, Optional<String> customAliasOpt) {
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(fullUrl);

    if (customAliasOpt.isPresent()) {
      String requestedAlias = customAliasOpt.get();
      log.info("Custom alias requested: {}", requestedAlias);
      validateCustomAlias(requestedAlias);
      return saveMapping(requestedAlias, normalizedUrl);
    }

    String candidate = generateUniqueAlias();
    if (urlMappingRepository.existsByAlias(candidate)) {
      // treat as failure
      throw new AliasCollisionException();
    }
    return saveMapping(candidate, normalizedUrl);
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

  private String generateUniqueAlias() {
    for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
      String candidate = aliasGenerator.generate();
      if (!aliasExists(candidate)) return candidate;
    }
    throw new AliasGenerationFailureException(MAX_GENERATION_ATTEMPTS);
  }

  private void validateCustomAlias(String alias) {
    log.info("Validating custom alias: {}", alias);
    if (!alias.matches(ALIAS_REGEX)) {
      throw new InvalidAliasException(alias);
    }
    if (aliasExists(alias)) {
      throw new DuplicateAliasException(alias);
    }
  }

  private boolean aliasExists(String candidate) {
    return urlMappingRepository.existsByAlias(candidate);
  }

  private UrlMapping saveMapping(String alias, String fullUrl) {
    log.info("Saving URL mapping: {} -> {}", alias, fullUrl);
    String shortUrl =
        UriComponentsBuilder.fromUriString(baseUrl).pathSegment(alias).build().toUriString();
    UrlMapping mapping =
        UrlMapping.builder()
            .alias(alias)
            .fullUrl(fullUrl)
            .shortUrl(shortUrl)
            .createdAt(LocalDateTime.now())
            .build();
    return urlMappingRepository.save(mapping);
  }
}
