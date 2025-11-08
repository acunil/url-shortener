package com.lucian.urlshortener.exception;

public class DuplicateAliasException extends IllegalArgumentException {
  public DuplicateAliasException(String alias) {
    super("Alias already exists: " + alias);
  }
}
