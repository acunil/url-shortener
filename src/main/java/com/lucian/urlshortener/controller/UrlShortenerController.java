package com.lucian.urlshortener.controller;

import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.dto.UrlResponse;
import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.service.UrlShortenerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
public class UrlShortenerController {

  private final UrlShortenerService urlShortenerService;

  @Operation(
      summary = "Shorten a URL",
      description = "Creates a shortened URL for the given full URL.")
  @ApiResponse(
      responseCode = "201",
      description = "URL shortened successfully",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UrlResponse.class)))
  @ApiResponse(responseCode = "400", description = "Invalid URL provided")
  @PostMapping("/shorten")
  public ResponseEntity<UrlResponse> shortenUrl(@RequestBody @Valid UrlRequest request) {
    log.info(
        "Received shorten URL request for {} with customAlias '{}'", request.fullUrl(), request.customAlias());
    UrlMapping urlMapping =
        urlShortenerService.createShortUrl(request.fullUrl(), request.customAlias());
    UrlResponse response =
        UrlResponse.builder()
            .shortUrl(urlMapping.getShortUrl())
            .fullUrl(urlMapping.getFullUrl())
            .alias(urlMapping.getAlias())
            .build();
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @Operation(
      summary = "Redirect to full URL",
      description = "Redirects to the original full URL for the given alias.")
  @ApiResponse(responseCode = "302", description = "Redirecting to full URL")
  @ApiResponse(responseCode = "404", description = "Alias not found")
  @GetMapping("/{alias}")
  public ResponseEntity<UrlResponse> redirect(@PathVariable String alias) {
    UrlMapping mapping = urlShortenerService.getByAlias(alias);
    URI location = URI.create(mapping.getFullUrl());
    return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
  }

  @DeleteMapping("/{alias}")
  public ResponseEntity<?> delete(@PathVariable String alias) {
    // return 204 or 404
    return null;
  }

  @GetMapping("/urls")
  public ResponseEntity<List<UrlResponse>> listAll() {
    // return all mappings
    return null;
  }
}
