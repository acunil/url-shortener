package com.lucian.urlshortener.exception;

public class InvalidUrlException extends IllegalArgumentException {
  public InvalidUrlException(String message) {
    super(message);
  }

  public InvalidUrlException(String message, Throwable cause) {
    super(message, cause);
  }
}
