package com.lucian.urlshortener.controller;

import com.lucian.urlshortener.dto.ErrorResponse;
import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.dto.UrlResponse;
import com.lucian.urlshortener.entity.UrlMapping;
import com.lucian.urlshortener.service.UrlShortenerService;
import com.lucian.urlshortener.utility.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "URL Shortener", description = "Simple RESTful API for shortening URLs.")
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
      description = "URL successfully shortened",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UrlResponse.class)))
  @ApiResponse(
      responseCode = "400",
      description = "Invalid input or alias already taken",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @PostMapping("/shorten")
  public ResponseEntity<UrlResponse> createShortUrl(@RequestBody @Valid UrlRequest request) {
    log.info(
        "Received shorten URL request for {} with customAlias '{}'",
        request.fullUrl(),
        request.customAlias());
    UrlMapping urlMapping =
        urlShortenerService.createShortUrl(request.fullUrl(), request.customAlias());
    return ResponseEntity.status(HttpStatus.CREATED).body(Mapper.toUrlResponse(urlMapping));
  }

  @Operation(
      summary = "Redirect to full URL",
      description = "Redirects to the original full URL for the given alias.")
  @ApiResponse(
      responseCode = "302",
      description = "Redirect to the original URL",
      content = @Content(mediaType = "application/json"))
  @ApiResponse(
      responseCode = "404",
      description = "Alias not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @GetMapping("/{alias}")
  public ResponseEntity<Void> getRedirect(
      @Parameter(name = "alias", description = "The alias to look up", required = true)
          @PathVariable
          String alias) {
    UrlMapping mapping = urlShortenerService.getByAlias(alias);
    URI location = URI.create(mapping.getFullUrl());
    return ResponseEntity.status(HttpStatus.FOUND).location(location).build();
  }

  @Operation(
      summary = "Delete a shortened URL",
      description = "Deletes the URL mapping for the given alias.")
  @ApiResponse(
      responseCode = "204",
      description = "Successfully deleted",
      content = @Content(mediaType = "application/json"))
  @ApiResponse(
      responseCode = "404",
      description = "Alias not found",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))
  @DeleteMapping("/{alias}")
  public ResponseEntity<Void> deleteAlias(
      @Parameter(name = "alias", description = "The alias to look up", required = true)
          @PathVariable
          String alias) {
    urlShortenerService.deleteByAlias(alias);
    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "List all shortened URLs",
      description = "Retrieves a list of all URL mappings.")
  @ApiResponse(
      responseCode = "200",
      description = "A list of shortened URLs",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = UrlResponse[].class)))
  @GetMapping("/urls")
  public ResponseEntity<List<UrlResponse>> listShortenedUrls() {
    List<UrlMapping> mappings = urlShortenerService.listAll();
    List<UrlResponse> responses = mappings.stream().map(Mapper::toUrlResponse).toList();
    return ResponseEntity.ok(responses);
  }
}
