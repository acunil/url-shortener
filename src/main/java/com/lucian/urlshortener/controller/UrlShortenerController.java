package com.lucian.urlshortener.controller;

import com.lucian.urlshortener.dto.UrlRequest;
import com.lucian.urlshortener.dto.UrlResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UrlShortenerController {

  @PostMapping("/shorten")
  public ResponseEntity<UrlResponse> shortenUrl(@RequestBody @Valid UrlRequest request) {
    // return 201 or 400
    return null;
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
