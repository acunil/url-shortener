package com.lucian.urlshortener.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI urlShortenerOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("URL Shortener API")
            .version("1.0.0")
            .description("Simple RESTful API for shortening URLs"));
  }
}
