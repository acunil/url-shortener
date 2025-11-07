package com.lucian.urlshortener.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.exception.AliasCollisionException;
import com.lucian.urlshortener.exception.AliasNotFoundException;
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

    UrlMapping urlMapping = urlShortenerService.createShortUrl(FULL_URL, REQUESTED_ALIAS);

    assertThat(urlMapping.getFullUrl()).isEqualTo(FULL_URL);
    assertThat(urlMapping.getAlias()).isEqualTo(REQUESTED_ALIAS);
    assertThat(urlMapping.getShortUrl()).contains("/" + REQUESTED_ALIAS);

    verify(urlMappingRepository).save(urlMapping);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Custom alias requested: myAlias",
            "Validating custom alias: myAlias",
            "Building URL mapping: myAlias -> https://www.example.com");
  }

  @Test
  void testCreateShortUrl_GeneratedAlias() {
    when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArguments()[0]);
    String generatedAlias = "abc123";
    when(aliasGenerator.generate()).thenReturn(generatedAlias);
    when(urlMappingRepository.existsByAlias(generatedAlias)).thenReturn(false);
    UrlMapping urlMapping = urlShortenerService.createShortUrl(FULL_URL, null);

    assertThat(urlMapping.getFullUrl()).isEqualTo(FULL_URL);
    assertThat(urlMapping.getShortUrl()).isEqualTo(BASE_URL + generatedAlias);

    verify(urlMappingRepository).save(urlMapping);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Generating unique alias", "Building URL mapping: abc123 -> https://www.example.com");
  }

  @Test
  void testCreateShortUrl_CustomAliasAlreadyExists() {
    when(urlMappingRepository.existsByAlias(REQUESTED_ALIAS)).thenReturn(true);
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, REQUESTED_ALIAS))
        .isInstanceOf(DuplicateAliasException.class)
        .hasMessage("Alias already exists: " + REQUESTED_ALIAS);

    verify(urlMappingRepository).existsByAlias(REQUESTED_ALIAS);
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Custom alias requested: myAlias", "Validating custom alias: myAlias");
  }

  @Test
  void testCreateShortUrl_InvalidCustomAlias() {
    String invalidAlias = "ab"; // too short
    when(urlMappingRepository.existsByAlias(invalidAlias)).thenReturn(false);
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, invalidAlias))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Invalid alias format");

    verifyNoInteractions(urlMappingRepository);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Custom alias requested: ab", "Validating custom alias: ab");
  }

  @Test
  void testCreateShortUrl_AliasGenerationFailure() {
    when(urlMappingRepository.existsByAlias(anyString())).thenReturn(true);
    when(aliasGenerator.generate()).thenReturn("alias1", "alias2", "alias3", "alias4", "alias5");
    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, null))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("Failed to generate a unique alias after 5 attempts");

    verify(urlMappingRepository, times(5)).existsByAlias(anyString());
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs()).containsExactly("Generating unique alias");
  }

  @Test
  void testCreateShortUrl_AliasCollision() {
    String generatedAlias = "abc123";
    when(urlMappingRepository.existsByAlias(generatedAlias))
        .thenReturn(false) // first attempt succeeds
        .thenReturn(true); // final check collides due to race
    when(aliasGenerator.generate()).thenReturn(generatedAlias);
    when(urlMappingRepository.save(any(UrlMapping.class))).thenAnswer(i -> i.getArguments()[0]);

    assertThatThrownBy(() -> urlShortenerService.createShortUrl(FULL_URL, null))
        .isInstanceOf(AliasCollisionException.class)
        .hasMessage("Generated alias collision, try again");

    verify(urlMappingRepository, times(2)).existsByAlias(anyString());
    verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    assertThat(logCaptor.getInfoLogs()).containsExactly("Generating unique alias");
  }

  @Test
  void testGetByAlias_found() {
    UrlMapping urlMapping =
        UrlMapping.builder()
            .alias(REQUESTED_ALIAS)
            .fullUrl(FULL_URL)
            .shortUrl(BASE_URL + REQUESTED_ALIAS)
            .build();
    when(urlMappingRepository.findById(REQUESTED_ALIAS)).thenReturn(Optional.of(urlMapping));

    UrlMapping result = urlShortenerService.getByAlias(REQUESTED_ALIAS);

    assertThat(result).isEqualTo(urlMapping);
    verify(urlMappingRepository).findById(REQUESTED_ALIAS);
    assertThat(logCaptor.getInfoLogs()).containsExactly("Retrieving URL mapping for alias: myAlias");
  }

  @Test
  void testGetByAlias_notFound() {
    when(urlMappingRepository.findById(REQUESTED_ALIAS)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> urlShortenerService.getByAlias(REQUESTED_ALIAS))
        .isInstanceOf(AliasNotFoundException.class)
        .hasMessage("Alias not found: " + REQUESTED_ALIAS);

    verify(urlMappingRepository).findById(REQUESTED_ALIAS);
    assertThat(logCaptor.getInfoLogs()).containsExactly("Retrieving URL mapping for alias: myAlias");
  }

  @Test
  void testDeleteByAlias_found() {
    when(urlMappingRepository.existsByAlias(REQUESTED_ALIAS)).thenReturn(true);
    urlShortenerService.deleteByAlias(REQUESTED_ALIAS);
    verify(urlMappingRepository).existsByAlias(REQUESTED_ALIAS);
    verify(urlMappingRepository).deleteById(REQUESTED_ALIAS);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Deleting URL mapping for alias: myAlias");
  }

  @Test
  void testDeleteByAlias_notFound() {
    when(urlMappingRepository.existsByAlias(REQUESTED_ALIAS)).thenReturn(false);
    assertThatThrownBy(() -> urlShortenerService.deleteByAlias(REQUESTED_ALIAS))
        .isInstanceOf(AliasNotFoundException.class)
        .hasMessage("Alias not found: " + REQUESTED_ALIAS);
    verify(urlMappingRepository).existsByAlias(REQUESTED_ALIAS);
    verify(urlMappingRepository, never()).deleteById(anyString());
    assertThat(logCaptor.getInfoLogs()).containsExactly("Deleting URL mapping for alias: myAlias");
    assertThat(logCaptor.getWarnLogs()).containsExactly("Alias not found for deletion: myAlias");
  }
}
