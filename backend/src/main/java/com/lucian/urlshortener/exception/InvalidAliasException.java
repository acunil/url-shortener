package com.lucian.urlshortener.exception;

import lombok.Getter;

@Getter
public class InvalidAliasException extends IllegalArgumentException {
  public enum Reason {
    TOO_SHORT,
    TOO_LONG,
    INVALID_CHARACTERS,
    NULL_ALIAS
  }

  private final Reason reason;
  private final String alias;

  public InvalidAliasException(Reason reason, String alias) {
    super(buildMessage(reason, alias));
    this.reason = reason;
    this.alias = alias;
  }

  private static String buildMessage(Reason reason, String alias) {
    return switch (reason) {
      case TOO_SHORT ->
          String.format("Invalid alias '%s': must be at least 3 characters long", alias);
      case TOO_LONG ->
          String.format("Invalid alias '%s': must be at most 64 characters long", alias);
      case INVALID_CHARACTERS ->
          String.format(
              "Invalid alias '%s': may contain only letters, digits, hyphens and underscores",
              alias);
      case NULL_ALIAS -> "Invalid alias 'null': alias must not be null";
    };
  }
}
