package com.lucian.urlshortener.controller;

import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.dto.UrlResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UrlShortenerController {

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
    // return 201 or 400
    return ResponseEntity.ok(new UrlResponse("alias", "fullUrl", "shortUrl"));
  }

  @GetMapping("/{alias}")
  public ResponseEntity<UrlResponse> redirect(@PathVariable String alias) {
    // return 302 or 404
    return null;
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
