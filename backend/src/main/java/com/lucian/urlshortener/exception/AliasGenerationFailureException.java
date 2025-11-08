package com.lucian.urlshortener.exception;

public class AliasGenerationFailureException extends IllegalStateException {
  public AliasGenerationFailureException(int attempts) {
    super(String.format("Failed to generate a unique alias after %d attempts", attempts));
  }
}
