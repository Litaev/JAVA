package com.example.sb.schemas;

import java.time.LocalDateTime;
import lombok.Data;

/**
 * Standard error response structure for API error handling.
 *
 * <p>Contains timestamp, HTTP status, error type, message, and request path.
 * Automatically sets timestamp to current time upon instantiation.
 */
@Data
public class ErrorResponse {

  /** The timestamp when the error occurred (auto-set to creation time). */
  private LocalDateTime timestamp;

  /** The HTTP status code (e.g., 400, 404, 500). */
  private int status;

  /** The error type (e.g., "Bad Request", "Not Found"). */
  private String error;

  /** Detailed error message describing what went wrong. */
  private String message;

  /** The request path that caused the error (optional). */
  private String path;

  /**
   * Constructs a new ErrorResponse with current timestamp.
   */
  public ErrorResponse() {
    this.timestamp = LocalDateTime.now();
  }
}