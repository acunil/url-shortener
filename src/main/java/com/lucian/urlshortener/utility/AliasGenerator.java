package com.lucian.urlshortener.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Slf4j
@Component
public final class AliasGenerator {

  private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private final SecureRandom rnd = new SecureRandom();
  private final int length;

  public AliasGenerator(@Value("${app.alias.length:7}") int length) {
    this.length = length;
  }

  public String generate() {
    var sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
    }
    return sb.toString();
  }
}
