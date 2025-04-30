package com.example.sb.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom API exception that includes HTTP status information.
 *
 * <p>This exception is used to provide more detailed error information to API clients,
 * including both a message and the appropriate HTTP status code.
 */
@Getter
public class ApiException extends RuntimeException {

  private final HttpStatus status;

  /**
   * Constructs a new API exception with the specified message and HTTP status.
   *
   * @param message the detail message
   * @param status the HTTP status code
   */
  public ApiException(String message, HttpStatus status) {
    super(message);
    this.status = status;
  }
}