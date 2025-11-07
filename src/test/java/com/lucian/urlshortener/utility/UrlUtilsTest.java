package com.lucian.urlshortener.utility;

import com.lucian.urlshortener.exception.InvalidUrlException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UrlUtilsTest {


  @Test
  void testNormalizeAndValidateUrl_ValidUrl() {
    String url = "https://www.example.com/path?query=param";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(url);
    assertThat(normalizedUrl).isEqualTo(url);
  }

  @Test
  void testNormalizeAndValidateUrl_UrlWithoutScheme() {
    String urlWithoutScheme = "www.example.com/path";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(urlWithoutScheme);
    assertThat(normalizedUrl).isEqualTo("https://www.example.com/path");
  }

  @Test
  void testNormalizeAndValidateUrl_UrlWithHttpScheme() {
    String httpUrl = "http://www.example.com/path";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(httpUrl);
    assertThat(normalizedUrl).isEqualTo("http://www.example.com/path");
  }

  @Test
  void testNormalizeAndValidateUrl_InvalidUrl() {
    String invalidUrl = "ht!tp://invalid-url";
    assertThatThrownBy(() -> UrlUtils.normalizeAndValidateUrl(invalidUrl))
        .isInstanceOf(InvalidUrlException.class)
        .hasMessage("Invalid URL");
  }
  
}
