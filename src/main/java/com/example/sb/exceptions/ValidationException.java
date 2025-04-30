package com.example.sb.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Custom exception for validation errors with HTTP status information.
 *
 * <p>This exception is thrown when input validation fails and automatically includes
 * a BAD_REQUEST (400) HTTP status code. It extends RuntimeException to be unchecked.
 */
@Getter
public class ValidationException extends RuntimeException {

  /** The HTTP status code associated with this exception (always BAD_REQUEST). */
  private final HttpStatus status;

  /**
   * Constructs a new ValidationException with the specified error message.
   *
   * <p>The status is automatically set to HttpStatus.BAD_REQUEST (400).
   *
   * @param message the detail message describing the validation failure
   */
  public ValidationException(String message) {
    super(message);
    this.status = HttpStatus.BAD_REQUEST;
  }
}