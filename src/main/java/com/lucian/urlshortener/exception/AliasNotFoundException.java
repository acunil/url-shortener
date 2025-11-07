package com.lucian.urlshortener.exception;

public class AliasNotFoundException extends RuntimeException {
  public AliasNotFoundException(String message) {
    super(message);
  }
}
