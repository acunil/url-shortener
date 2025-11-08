package com.lucian.urlshortener.utility;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lucian.urlshortener.exception.InvalidUrlException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

class UrlUtilsTest {

  LogCaptor logCaptor = LogCaptor.forClass(UrlUtils.class);

  @Test
  void testNormalizeAndValidateUrl_ValidUrl() {
    String url = "https://www.example.com/path?query=param";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(url);
    assertThat(normalizedUrl).isEqualTo(url);
    assertThat(logCaptor.getInfoLogs())
        .containsExactly(
            "Normalizing and validating URL: https://www.example.com/path?query=param");
    assertThat(logCaptor.getErrorLogs()).isEmpty();
  }

  @Test
  void testNormalizeAndValidateUrl_UrlWithoutScheme() {
    String urlWithoutScheme = "www.example.com/path";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(urlWithoutScheme);
    assertThat(normalizedUrl).isEqualTo("https://www.example.com/path");
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: www.example.com/path");
    assertThat(logCaptor.getErrorLogs()).isEmpty();
  }

  @Test
  void testNormalizeAndValidateUrl_UrlWithHttpScheme() {
    String httpUrl = "http://www.example.com/path";
    String normalizedUrl = UrlUtils.normalizeAndValidateUrl(httpUrl);
    assertThat(normalizedUrl).isEqualTo("http://www.example.com/path");
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: http://www.example.com/path");
    assertThat(logCaptor.getErrorLogs()).isEmpty();
  }

  @Test
  void testNormalizeAndValidateUrl_InvalidUrl() {
    String invalidUrl = "ht!tp://invalid-url";
    assertThatThrownBy(() -> UrlUtils.normalizeAndValidateUrl(invalidUrl))
        .isInstanceOf(InvalidUrlException.class)
        .hasMessage("Invalid URL");
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: ht!tp://invalid-url");
    assertThat(logCaptor.getErrorLogs()).containsExactly("Invalid URL syntax: ht!tp://invalid-url");
  }

  @Test
  void testNormalizeAndValidateUrl_UnsupportedScheme() {
    String ftpUrl = "ftp://www.example.com/path";
    assertThatThrownBy(() -> UrlUtils.normalizeAndValidateUrl(ftpUrl))
        .isInstanceOf(InvalidUrlException.class)
        .hasMessage("Unsupported URL scheme: ftp");
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: ftp://www.example.com/path");
    assertThat(logCaptor.getErrorLogs())
        .containsExactly("Unsupported URL scheme: ftp");
  }

  @Test
  void testNormalizeAndValidateUrl_UrlWithoutHost() {
    String urlWithoutHost = "https:///path";
    assertThatThrownBy(() -> UrlUtils.normalizeAndValidateUrl(urlWithoutHost))
        .isInstanceOf(InvalidUrlException.class)
        .hasMessage("URL must include a host: https:///path");
    assertThat(logCaptor.getInfoLogs())
        .containsExactly("Normalizing and validating URL: https:///path");
    assertThat(logCaptor.getErrorLogs())
        .containsExactly("URL must include a host: https:///path");
  }
}
