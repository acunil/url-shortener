package com.lucian.urlshortener.exception;

public class InvalidAliasException extends IllegalArgumentException {
  public InvalidAliasException(String alias) {
    super("Invalid alias format: '%s'".formatted(alias));
  }
}
