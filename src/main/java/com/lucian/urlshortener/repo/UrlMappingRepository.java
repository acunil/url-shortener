package com.lucian.urlshortener.repo;

import com.lucian.urlshortener.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, String> {
  boolean existsByAlias(String alias);
}
