package com.lucian.urlshortener.exception;

public class InvalidAliasException extends IllegalArgumentException {
  public InvalidAliasException(String message) {
    super(message);
  }
}
