package com.example.sb.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Swagger/OpenAPI documentation.
 */
@Configuration
public class SwaggerConfig {

  /**
   * Creates and configures the OpenAPI documentation bean.
   *
   * @return configured OpenAPI object with API information
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Car Service API")
            .version("1.0")
            .description("API for managing users and their cars")
            .license(new License().name("Apache 2.0").url("http://springdoc.org")));
  }
}