package com.lucian.urlshortener.service;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.exception.*;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import com.lucian.urlshortener.utility.AliasGenerator;
import com.lucian.urlshortener.utility.UrlUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  public static final Set<String> RESERVED_ALIAS = Set.of("urls", "shorten");

  public static final String ALIAS_REGEX = "[A-Za-z0-9\\-_]{3,64}";
  private static final int MAX_GENERATION_ATTEMPTS = 5;

  @Transactional
  public UrlMapping createShortUrl(String fullUrl, String customAlias) {
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(fullUrl);
    String aliasToUse;
    if (customAlias != null) {
      log.info("Custom alias requested: {}", customAlias);
      validateCustomAlias(customAlias);
      aliasToUse = customAlias;
    } else {
      aliasToUse = generateUniqueAlias();
      if (aliasExists(aliasToUse)) {
        throw new AliasCollisionException();
      }
    }
    UrlMapping mapping = buildMapping(aliasToUse, normalizedUrl);
    return urlMappingRepository.save(mapping);
  }

  public UrlMapping getByAlias(String alias) {
    log.info("Retrieving URL mapping for alias: {}", alias);
    Optional<UrlMapping> mappingOptional = urlMappingRepository.findById(alias);
    return mappingOptional.orElseThrow(() -> new AliasNotFoundException(alias));
  }

  public void deleteByAlias(String alias) {
    log.info("Deleting URL mapping for alias: {}", alias);
    if (!aliasExists(alias)) {
      log.warn("Alias not found for deletion: {}", alias);
      throw new AliasNotFoundException(alias);
    }
    urlMappingRepository.deleteById(alias);
  }

  public List<UrlMapping> listAll() {
    log.info("Listing all URL mappings");
    return urlMappingRepository.findAll();
  }

  private String generateUniqueAlias() {
    log.info("Generating unique alias");
    for (int i = 0; i < MAX_GENERATION_ATTEMPTS; i++) {
      String candidate = aliasGenerator.generate();
      if (!aliasExists(candidate)) return candidate;
    }
    throw new AliasGenerationFailureException(MAX_GENERATION_ATTEMPTS);
  }

  private void validateCustomAlias(String alias) {
    log.info("Validating custom alias: {}", alias);
    if (RESERVED_ALIAS.contains(alias.toLowerCase())) {
      throw new ReservedAliasException(alias);
    }
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

  private UrlMapping buildMapping(String alias, String fullUrl) {
    log.info("Building URL mapping: {} -> {}", alias, fullUrl);
    String shortUrl =
        UriComponentsBuilder.fromUriString(baseUrl).pathSegment(alias).build().toUriString();
    return UrlMapping.builder()
        .alias(alias)
        .fullUrl(fullUrl)
        .shortUrl(shortUrl)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
