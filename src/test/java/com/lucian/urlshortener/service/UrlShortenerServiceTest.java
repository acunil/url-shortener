package com.lucian.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class UrlShortenerServiceTest {

  UrlShortenerService urlShortenerService;
  @Mock UrlMappingRepository urlMappingRepository;

  @BeforeEach
  void setUp() {
    urlShortenerService = new UrlShortenerService(urlMappingRepository);
  }

  @Test
  void testCreateShortUrl() {

    UrlMapping urlMapping =
        urlShortenerService.createShortUrl("https://www.example.com", Optional.of("myAlias"));

    assertThat(urlMapping.getFullUrl()).isEqualTo("https://www.example.com");
    assertThat(urlMapping.getAlias()).isEqualTo("myAlias");
    assertThat(urlMapping.getShortUrl()).isNotNull();

    verify(urlMappingRepository).save(urlMapping);
  }
}
