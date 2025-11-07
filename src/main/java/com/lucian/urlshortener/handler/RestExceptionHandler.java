package com.lucian.urlshortener.handler;

import com.lucian.urlshortener.dto.ErrorResponse;
import com.lucian.urlshortener.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(AliasNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(
      AliasNotFoundException ex, HttpServletRequest request) {
    return buildErrorResponse(ex, HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(DuplicateAliasException.class)
  public ResponseEntity<ErrorResponse> handleDuplicate(
      DuplicateAliasException ex, HttpServletRequest request) {
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(AliasGenerationFailureException.class)
  public ResponseEntity<ErrorResponse> handleAliasGenerationFailure(
      AliasGenerationFailureException ex, HttpServletRequest request) {
    return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(InvalidAliasException.class)
  public ResponseEntity<ErrorResponse> handleInvalidAlias(
      InvalidAliasException ex, HttpServletRequest request) {
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
  }

  @ExceptionHandler(InvalidUrlException.class)
  public ResponseEntity<ErrorResponse> handleInvalidUrl(
      InvalidUrlException ex, HttpServletRequest request) {
    return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
  }

  private ResponseEntity<ErrorResponse> buildErrorResponse(
      Exception ex, HttpStatus status, HttpServletRequest request) {
    ErrorResponse body =
        ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();
    return ResponseEntity.status(status).body(body);
  }
}
