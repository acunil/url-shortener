package com.lucian.urlshortener.utility;

import com.lucian.urlshortener.exception.InvalidAliasException;
import com.lucian.urlshortener.exception.ReservedAliasException;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.regex.Pattern;

import static com.lucian.urlshortener.exception.InvalidAliasException.Reason.*;

@Slf4j
public final class AliasValidator {
  public static final Set<String> RESERVED_ALIAS = Set.of("urls", "shorten");

  private static final Pattern VALID_ALIAS = Pattern.compile("^[A-Za-z0-9_-]+$");
  private static final int MIN_LEN = 3;
  private static final int MAX_LEN = 64;

  private AliasValidator() {
    // Default constructor
  }

  public static void validateOrThrow(String alias) {
    log.info("Validating custom alias: {}", alias);
    if (alias == null) {
      throw new InvalidAliasException(NULL_ALIAS, "null");
    }
    if (RESERVED_ALIAS.contains(alias.toLowerCase())) {
      throw new ReservedAliasException(alias);
    }
    int len = alias.length();
    if (len < MIN_LEN) {
      throw new InvalidAliasException(TOO_SHORT, alias);
    }
    if (len > MAX_LEN) {
      throw new InvalidAliasException(TOO_LONG, alias);
    }
    if (!VALID_ALIAS.matcher(alias).matches()) {
      throw new InvalidAliasException(INVALID_CHARACTERS, alias);
    }
  }
}