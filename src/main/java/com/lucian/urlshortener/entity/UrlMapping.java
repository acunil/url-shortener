package com.lucian.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class UrlMapping {
  @Id
  private String alias;

  @Column(nullable = false)
  private String fullUrl;

  @Column(nullable = false)
  private String shortUrl;

  private LocalDateTime createdAt;
}
