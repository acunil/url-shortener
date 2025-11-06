package com.lucian.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
  @Id
  private String alias;

  @Column(nullable = false)
  private String fullUrl;

  @Column(nullable = false)
  private String shortUrl;

  private LocalDateTime createdAt;
}
