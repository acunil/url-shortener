package com.lucian.urlshortener.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.dto.UrlResponse;
import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.repo.UrlMappingRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UrlShortenerControllerTest {

  public static final String SHORTEN_ENDPOINT = "/shorten";
  public static final String LOCALHOST = "http://localhost/";

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @Autowired UrlMappingRepository urlMappingRepository;

  String fullUrl;
  String alias;
  UrlMapping urlMapping;

  @BeforeEach
  void setUp() {
    fullUrl = "https://www.example.com";
    alias = "myAlias";
    urlMapping =
        UrlMapping.builder()
            .alias(alias)
            .fullUrl(fullUrl)
            .shortUrl(LOCALHOST + alias)
            .build();
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
    urlMappingRepository.save(urlMapping);

    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Alias already exists: " + alias));
  }

  @Test
  void shortenUrl_NoAlias_GeneratesAlias() throws Exception {
    UrlRequest request = new UrlRequest(fullUrl, null);
    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fullUrl").value(fullUrl))
        .andExpect(jsonPath("$.alias").isNotEmpty())
        .andExpect(jsonPath("$.shortUrl").value(startsWith(LOCALHOST)));
  }

  @Test
  void shortenUrl_InvalidAlias_ReturnsBadRequest() throws Exception {
    UrlRequest request = new UrlRequest(fullUrl, "invalid alias!");
    mockMvc
        .perform(
            post(SHORTEN_ENDPOINT)
                .contentType(APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid alias format: 'invalid alias!'"));
  }

  @Test
  void getUrlMapping_ExistingAlias_ReturnsRedirect() throws Exception {
    urlMappingRepository.save(urlMapping);
    mockMvc
        .perform(get("/" + alias).contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", fullUrl));
  }

  @Test
  void getUrlMapping_NonExistingAlias_ReturnsNotFound() throws Exception {
    mockMvc
        .perform(get("/nonExistingAlias").contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Alias not found: nonExistingAlias"));
  }

  @Test
  void deleteUrlMapping_ExistingAlias_ReturnsNoContent() throws Exception {
    urlMappingRepository.save(urlMapping);
    assertThat(urlMappingRepository.existsByAlias(alias)).isTrue();

    mockMvc
        .perform(delete("/" + alias).contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isNoContent());

    assertThat(urlMappingRepository.existsByAlias(alias)).isFalse();
  }

  @Test
  void deleteUrlMapping_NonExistingAlias_ReturnsNotFound() throws Exception {
    mockMvc
        .perform(delete("/nonExistingAlias").contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Alias not found: nonExistingAlias"));
  }

  @Test
  void listAllUrlMappings_ReturnsOk() throws Exception {
    UrlMapping mapping2 =
        UrlMapping.builder()
            .alias("alias2")
            .fullUrl("https://example2.com")
            .shortUrl(LOCALHOST + "alias2")
            .build();
    urlMappingRepository.save(urlMapping);
    urlMappingRepository.save(mapping2);

    MvcResult mvcResult =
        mockMvc
            .perform(get("/urls").contentType(APPLICATION_JSON_VALUE))
            .andExpect(status().isOk())
            .andReturn();

    List<UrlResponse> responses =
        objectMapper.readValue(
            mvcResult.getResponse().getContentAsString(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, UrlResponse.class));
    assertThat(responses)
        .hasSize(2)
        .anySatisfy(
            response -> {
              assertThat(response.alias()).isEqualTo(alias);
              assertThat(response.fullUrl()).isEqualTo(fullUrl);
              assertThat(response.shortUrl()).isEqualTo(LOCALHOST + alias);
            })
        .anySatisfy(
            response -> {
              assertThat(response.alias()).isEqualTo("alias2");
              assertThat(response.fullUrl()).isEqualTo("https://example2.com");
              assertThat(response.shortUrl()).isEqualTo(LOCALHOST + "alias2");
            });
  }
}
