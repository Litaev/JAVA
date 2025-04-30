package com.example.sb.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Validates that an email address is unique in the system.
 *
 * <p>When applied to an email field, checks that no existing user has the same email address.
 * Should be used with {@link UniqueEmailValidator} for the actual validation logic.
 *
 * <p>Example usage:
 * <pre>
 * {@code @UniqueEmail
 * private String email;}
 * </pre>
 */
@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {

  /**
   * Default error message when validation fails.
   *
   * @return the error message template
   */
  String message() default "Email already exists";

  /**
   * Groups for constraint composition.
   *
   * @return the validation groups
   */
  Class<?>[] groups() default {};

  /**
   * Payload for custom metadata.
   *
   * @return the payload classes
   */
  Class<? extends Payload>[] payload() default {};
}