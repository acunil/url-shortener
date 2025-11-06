package com.lucian.urlshortener.service;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.exception.AliasGenerationFailureException;
import com.lucian.urlshortener.exception.DuplicateAliasException;
import com.lucian.urlshortener.exception.InvalidAliasException;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import com.lucian.urlshortener.utility.AliasGenerator;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    String normalizedUrl = normalizeAndValidateUrl(fullUrl);

    if (customAliasOpt.isPresent()) {
      String requestedAlias = customAliasOpt.get();
      validateCustomAlias(requestedAlias);
      return saveMapping(requestedAlias, normalizedUrl);
    }

    String candidate = generateUniqueAlias();
    if (urlMappingRepository.existsByAlias(candidate)) {
      // treat as failure
      throw new IllegalStateException("Generated alias collision, try again");
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

  private String normalizeAndValidateUrl(String url) {
    try {
      URI uri = new URI(url);
      if (uri.getScheme() == null) {
        uri = new URI("https://" + url);
      }
      if (uri.getHost() == null) throw new IllegalArgumentException("URL must include a host");
      String scheme = uri.getScheme().toLowerCase();
      if (!scheme.equals("http") && !scheme.equals("https"))
        throw new IllegalArgumentException("Unsupported URL scheme");
      return uri.toString();
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid URL", e);
    }
  }

  private UrlMapping saveMapping(String alias, String fullUrl) {
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
