package com.lucian.urlshortener.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucian.urlshortener.dto.UrlRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UrlShortenerControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {}

  @Test
  void shortenUrl_ValidRequest_ReturnsCreated() throws Exception {
    UrlRequest request = new UrlRequest("https://www.example.com", "myAlias");
    String content = objectMapper.writeValueAsString(request);
    mockMvc
        .perform(post("/shorten").contentType("application/json").content(content))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.fullUrl").value("https://www.example.com"))
        .andExpect(jsonPath("$.alias").value("myAlias"))
        .andExpect(jsonPath("$.shortUrl").exists());
  }
}
