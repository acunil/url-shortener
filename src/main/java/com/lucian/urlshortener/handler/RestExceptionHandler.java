package com.lucian.urlshortener.handler;

import com.lucian.urlshortener.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(AliasNotFoundException.class)
  public ResponseEntity<Void> handleNotFound(AliasNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
  }

  @ExceptionHandler(DuplicateAliasException.class)
  public ResponseEntity<String> handleDuplicate(DuplicateAliasException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(AliasGenerationFailureException.class)
  public ResponseEntity<String> handleAliasGenerationFailure(AliasGenerationFailureException ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidAliasException.class)
  public ResponseEntity<String> handleInvalidAlias(InvalidAliasException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

  @ExceptionHandler(InvalidUrlException.class)
  public ResponseEntity<String> handleInvalidUrl(InvalidUrlException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }

}