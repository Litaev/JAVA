package com.example.sb.exceptions;

import com.example.sb.schemas.ErrorResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Global exception handler for REST controllers.
 *
 * <p>Handles various exceptions and returns appropriate HTTP responses with error details.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandlerController {

  /**
   * Handles EntityNotFoundException by returning a 404 response.
   *
   * @param ex the caught EntityNotFoundException
   * @return ResponseEntity with ErrorResponse containing not found details
   */
  @ExceptionHandler(EntityNotFoundException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Entity not found")
  })
  public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
    log.warn("Entity not found: {}, returning 404 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.NOT_FOUND.value());
    response.setError("Not Found");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * Handles validation errors from MethodArgumentNotValidException.
   *
   * @param ex the caught MethodArgumentNotValidException
   * @return ResponseEntity with ErrorResponse containing validation error details
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Validation error")
  })
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
    log.warn("Validation error: {}, returning 400 status", errorMessage);
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(errorMessage);
    return ResponseEntity.badRequest().body(response);
  }

  /**
   * Handles illegal argument exceptions.
   *
   * @param ex the caught IllegalArgumentException
   * @return ResponseEntity with ErrorResponse containing bad request details
   */
  @ExceptionHandler(IllegalArgumentException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Invalid argument")
  })
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    log.warn("Illegal argument: {}, returning 400 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles method argument type mismatch exceptions.
   *
   * @param ex the caught MethodArgumentTypeMismatchException
   * @return ResponseEntity with ErrorResponse containing type mismatch details
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Argument type mismatch")
  })
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    log.warn("Argument type mismatch: {}, returning 400 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles constraint violation exceptions.
   *
   * @param ex the caught ConstraintViolationException
   * @return ResponseEntity with ErrorResponse containing constraint violation details
   */
  @ExceptionHandler(ConstraintViolationException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Constraint violation")
  })
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.warn("Constraint violation: {}, returning 400 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles custom validation exceptions.
   *
   * @param ex the caught ValidationException
   * @return ResponseEntity with ErrorResponse containing validation error details
   */
  @ExceptionHandler(ValidationException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Validation failed")
  })
  public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
    log.warn("Validation failed: {}, returning 400 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles handler method validation exceptions.
   *
   * @param ex the caught HandlerMethodValidationException
   * @return ResponseEntity with ErrorResponse containing validation error details
   */
  @ExceptionHandler(HandlerMethodValidationException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Handler method validation failed")
  })
  public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {
    log.warn("Handler method validation failed: {}, returning 400 status", ex.getMessage());
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * Handles cases when no handler is found for request.
   *
   * @param ex the caught NoHandlerFoundException
   * @return ResponseEntity with ErrorResponse containing endpoint not found details
   */
  @ExceptionHandler(NoHandlerFoundException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "404", description = "Endpoint not found")
  })
  public ResponseEntity<ErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
    log.warn("No handler found for {} {}, returning 404 status",
        ex.getHttpMethod(), ex.getRequestURL());
    ErrorResponse response = new ErrorResponse();
    response.setError("No Handler Found");
    response.setStatus(HttpStatus.NOT_FOUND.value());
    response.setMessage(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * Handles all uncaught exceptions.
   *
   * @param ex the caught Exception
   * @return ResponseEntity with ErrorResponse containing internal server error details
   */
  @ExceptionHandler(Exception.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "500", description = "Unexpected server error")
  })
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred: {}, returning 500 status", ex.getMessage(), ex);
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.setError("Internal Error");
    response.setMessage("An unexpected error occurred");
    return ResponseEntity.internalServerError().body(response);
  }

  /**
   * Handles database integrity violation exceptions.
   *
   * @param ex the caught DataIntegrityViolationException
   * @return ResponseEntity with ErrorResponse containing constraint violation details
   */
  @ExceptionHandler(DataIntegrityViolationException.class)
  @ApiResponses(value = {
      @ApiResponse(responseCode = "400", description = "Database constraint violation")
  })
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {
    String rootCause = Objects.requireNonNull(ex.getRootCause()).getMessage();
    log.warn("Data integrity violation: {}, returning 400 status", rootCause);
    ErrorResponse response = new ErrorResponse();
    response.setStatus(HttpStatus.BAD_REQUEST.value());
    response.setError("Bad Request");
    response.setMessage("Database constraint violation: " + rootCause);
    return ResponseEntity.badRequest().body(response);
  }
}