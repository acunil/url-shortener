package com.lucian.urlshortener.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UrlShortenerControllerTest {

  public static final String SHORTEN_ENDPOINT = "/shorten";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired UrlMappingRepository urlMappingRepository;

  String fullUrl;
  String alias;

  @BeforeEach
  void setUp() {
    fullUrl = "https://www.example.com";
    alias = "myAlias";
  }

  @Test
  void shortenUrl_ValidRequest_ReturnsCreated() throws Exception {
    UrlRequest request = new UrlRequest(fullUrl, alias);
    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fullUrl").value(fullUrl))
        .andExpect(jsonPath("$.alias").value(alias))
        .andExpect(jsonPath("$.shortUrl").value("http://localhost/myAlias"));
  }

  @Test
  void shortenUrl_InvalidUrl_ReturnsBadRequest() throws Exception {
    UrlRequest request = new UrlRequest("ftp://upload.com", null);
    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Unsupported URL scheme: ftp"));
  }

  @Test
  void shortenUrl_CustomAliasAlreadyExists_ReturnsBadRequest() throws Exception {
    UrlRequest request = new UrlRequest(fullUrl, alias);
    UrlMapping existingMapping =
        UrlMapping.builder().shortUrl(fullUrl).alias(alias).fullUrl(fullUrl).build();
    urlMappingRepository.save(existingMapping);

    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Alias already exists: " + alias));
  }
}
