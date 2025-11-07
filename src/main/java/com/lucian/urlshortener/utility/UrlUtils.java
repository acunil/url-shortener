package com.lucian.urlshortener.utility;

import com.lucian.urlshortener.exception.InvalidUrlException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
public class UrlUtils {

  private UrlUtils() {
    // Default constructor
  }

  public static String normalizeAndValidateUrl(String url) {
    log.info("Normalizing and validating URL: {}", url);
    try {
      URI uri = new URI(url);
      if (uri.getScheme() == null) {
        uri = new URI("https://" + url);
      }
      if (uri.getHost() == null) throw new InvalidUrlException("URL must include a host: " + url);
      String scheme = uri.getScheme().toLowerCase();
      if (!scheme.equals("http") && !scheme.equals("https"))
        throw new InvalidUrlException("Unsupported URL scheme: " + scheme);
      return uri.toString();
    } catch (URISyntaxException e) {
      throw new InvalidUrlException("Invalid URL", e);
    }
  }

}
