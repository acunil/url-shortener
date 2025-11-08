package com.lucian.urlshortener.utility;

import static org.assertj.core.api.Assertions.*;

import com.lucian.urlshortener.exception.InvalidAliasException;
import com.lucian.urlshortener.exception.ReservedAliasException;
import org.junit.jupiter.api.Test;

class AliasValidatorTest {

  @Test
  void validateOrThrow_valid() {
    String validAlias = "valid_Alias-123";
    assertThatCode(() -> AliasValidator.validateOrThrow(validAlias)).doesNotThrowAnyException();
  }

  @Test
  void validateOrThrow_nullAlias_throwsException() {
    assertThatThrownBy(() -> AliasValidator.validateOrThrow(null))
        .isInstanceOf(InvalidAliasException.class)
        .hasMessageContaining("alias must not be null");
  }

  @Test
  void validateOrThrow_tooShortAlias_throwsException() {
    String shortAlias = "ab";
    assertThatThrownBy(() -> AliasValidator.validateOrThrow(shortAlias))
        .isInstanceOf(InvalidAliasException.class)
        .hasMessageContaining("must be at least 3 characters long");
  }

  @Test
  void validateOrThrow_tooLongAlias_throwsException() {
    String longAlias = "a".repeat(65);
    assertThatThrownBy(() -> AliasValidator.validateOrThrow(longAlias))
        .isInstanceOf(InvalidAliasException.class)
        .hasMessageContaining("must be at most 64 characters long");
  }

  @Test
  void validateOrThrow_invalidCharactersAlias_throwsException() {
    String invalidAlias = "invalid@alias!";
    assertThatThrownBy(() -> AliasValidator.validateOrThrow(invalidAlias))
        .isInstanceOf(InvalidAliasException.class)
        .hasMessageContaining("may contain only letters, digits, hyphens and underscores");
  }

  @Test
  void validateOrThrow_reservedAlias_throwsException() {
    String reservedAlias = "urls";
    assertThatThrownBy(() -> AliasValidator.validateOrThrow(reservedAlias))
        .isInstanceOf(ReservedAliasException.class)
        .hasMessageContaining("reserved");
  }
}
