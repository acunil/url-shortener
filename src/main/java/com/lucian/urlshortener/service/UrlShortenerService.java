package com.lucian.urlshortener.service;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UrlShortenerService {

  private final UrlMappingRepository urlMappingRepository;

  public UrlMapping createShortUrl(String fullUrl, Optional<String> alias) {
    // validate, check for alias duplication, generate shortUrl
    return null;
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
