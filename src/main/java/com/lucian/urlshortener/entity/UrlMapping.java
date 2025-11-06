package com.lucian.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

import lombok.*;

@Entity
@Table(name = "url_mapping")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UrlMapping {
  @Id private String alias;

  @Column(nullable = false)
  private String fullUrl;

  @Column(nullable = false)
  private String shortUrl;

  private LocalDateTime createdAt;
}
