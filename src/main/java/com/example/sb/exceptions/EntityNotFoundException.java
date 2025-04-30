package com.example.sb.exceptions;

/**
 * Exception thrown when a requested entity is not found in the system.
 *
 * <p>This exception typically indicates a 404 Not Found response should be returned
 * to the client when this exception is handled by the global exception handler.
 */
public class EntityNotFoundException extends RuntimeException {

  /**
   * Constructs a new EntityNotFoundException with the specified detail message.
   *
   * @param message the detail message describing which entity was not found
   */
  public EntityNotFoundException(String message) {
    super(message);
  }
}