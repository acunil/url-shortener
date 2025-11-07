package com.lucian.urlshortener.exception;

public class AliasCollisionException extends RuntimeException {
  public AliasCollisionException() {
    super("Generated alias collision, try again");
  }
}
