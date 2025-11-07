package com.lucian.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.exception.AliasCollisionException;
import com.lucian.urlshortener.exception.DuplicateAliasException;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import com.lucian.urlshortener.utility.AliasGenerator;
import java.util.Optional;
import nl.altindag.log.LogCaptor;
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
  @Mock AliasGenerator aliasGenerator;
  final LogCaptor logCaptor = LogCaptor.forClass(UrlShortenerService.class);

  static final String REQUESTED_ALIAS = "myAlias";
  static final String FULL_URL = "https://www.example.com";

  @BeforeEach
  void setUp() {
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
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Normalizing and validating URL: https://www.example.com",
            "Custom alias requested: myAlias",
            "Validating custom alias: myAlias",
            "Saving URL mapping: myAlias -> https://www.example.com");
  }

  @Test
  void testCreateShortUrl_GeneratedAlias() {
    when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArguments()[0]);
    String generatedAlias = "abc123";
    when(aliasGenerator.generate()).thenReturn(generatedAlias);
    when(urlMappingRepository.existsByAlias(generatedAlias)).thenReturn(false);
    UrlMapping urlMapping = urlShortenerService.createShortUrl(FULL_URL, Optional.empty());

    assertThat(urlMapping.getFullUrl()).isEqualTo(FULL_URL);
    assertThat(urlMapping.getShortUrl()).isEqualTo(BASE_URL + generatedAlias);

    verify(urlMappingRepository).save(urlMapping);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Normalizing and validating URL: https://www.example.com",
            "Saving URL mapping: " + generatedAlias + " -> https://www.example.com");
  }

  @Test
  void testCreateShortUrl_CustomAliasAlreadyExists() {
    when(urlMappingRepository.existsByAlias(REQUESTED_ALIAS)).thenReturn(true);
    Optional<String> requestedAliasOpt = Optional.of(REQUESTED_ALIAS);
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, requestedAliasOpt))
        .isInstanceOf(DuplicateAliasException.class)
        .hasMessage("Alias already exists: " + REQUESTED_ALIAS);

    verify(urlMappingRepository).existsByAlias(REQUESTED_ALIAS);
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Normalizing and validating URL: https://www.example.com",
            "Custom alias requested: myAlias",
            "Validating custom alias: myAlias");
  }

  @Test
  void testCreateShortUrl_InvalidCustomAlias() {
    String invalidAlias = "ab"; // too short
    when(urlMappingRepository.existsByAlias(invalidAlias)).thenReturn(false);
    Optional<String> requestedAliasOpt = Optional.of(invalidAlias);
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, requestedAliasOpt))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Invalid alias format");

    verifyNoInteractions(urlMappingRepository);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Normalizing and validating URL: https://www.example.com",
            "Custom alias requested: ab",
            "Validating custom alias: ab");
  }

  @Test
  void testCreateShortUrl_AliasGenerationFailure() {
    when(urlMappingRepository.existsByAlias(anyString())).thenReturn(true);
    when(aliasGenerator.generate()).thenReturn("alias1", "alias2", "alias3", "alias4", "alias5");
    Optional<String> requestedAliasOpt = Optional.empty();
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, requestedAliasOpt))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Failed to generate a unique alias after 5 attempts");

    verify(urlMappingRepository, times(5)).existsByAlias(anyString());
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: https://www.example.com");
  }

  @Test
  void testCreateShortUrl_AliasCollision() {
    String generatedAlias = "abc123";
    when(urlMappingRepository.existsByAlias(generatedAlias))
        .thenReturn(false) // first attempt succeeds
        .thenReturn(true); // final check collides due to race
    when(aliasGenerator.generate()).thenReturn(generatedAlias);
    when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArguments()[0]);

    Optional<String> noAlias = Optional.empty();
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, noAlias))
        .isInstanceOf(AliasCollisionException.class)
        .hasMessage("Generated alias collision, try again");

    verify(urlMappingRepository, times(2)).existsByAlias(anyString());
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: https://www.example.com");
  }
}
