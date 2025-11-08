package com.lucian.urlshortener.utility;

import com.lucian.urlshortener.exception.InvalidUrlException;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlUtils {

  private UrlUtils() {
    // Default constructor
  }

  public static String normalizeAndValidateUrl(String url) {
    // TODO consider allow www. prefixes
    log.info("Normalizing and validating URL: {}", url);
    try {
      URI uri = new URI(url);
      if (uri.getScheme() == null) {
        uri = new URI("https://" + url);
      }
      if (uri.getHost() == null) {
        log.error("URL must include a host: {}", url);
        throw new InvalidUrlException("URL must include a host: " + url);
      }
      String scheme = uri.getScheme().toLowerCase();
      if (!scheme.equals("http") && !scheme.equals("https")) {
        log.error("Unsupported URL scheme: {}", scheme);
        throw new InvalidUrlException("Unsupported URL scheme: " + scheme);
      }
      return uri.toString();
    } catch (URISyntaxException e) {
      log.error("Invalid URL syntax: {}", url, e);
      throw new InvalidUrlException("Invalid URL", e);
    }
  }
}
