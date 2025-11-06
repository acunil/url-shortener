package com.lucian.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import com.lucian.urlshortener.utility.AliasGenerator;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UrlShortenerServiceTest {

  static final String BASE_URL = "http://localhost/";

  UrlShortenerService urlShortenerService;
  @Mock UrlMappingRepository urlMappingRepository;
  AliasGenerator aliasGenerator;

  static final String REQUESTED_ALIAS = "myAlias";
  static final String FULL_URL = "https://www.example.com";

  @BeforeEach
  void setUp() {
    aliasGenerator = new AliasGenerator(6);
    urlShortenerService = new UrlShortenerService(urlMappingRepository, aliasGenerator, BASE_URL);
  }

  @Test
  void testCreateShortUrl() {
    when(urlMappingRepository.existsByAlias(REQUESTED_ALIAS)).thenReturn(false);
    when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArguments()[0]);

    UrlMapping urlMapping =
        urlShortenerService.createShortUrl(FULL_URL, Optional.of(REQUESTED_ALIAS));

    assertThat(urlMapping.getFullUrl()).isEqualTo(FULL_URL);
    assertThat(urlMapping.getAlias()).isEqualTo(REQUESTED_ALIAS);
    assertThat(urlMapping.getShortUrl()).contains("/" + REQUESTED_ALIAS);

    verify(urlMappingRepository).save(urlMapping);
  }
}
