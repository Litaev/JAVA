
package com.example.sb.aspects;

import com.example.sb.exceptions.EntityNotFoundException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging method entry, exit and exceptions in controllers and exception handlers.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

  /**
   * Logs method entry, exit and execution time around method execution.
   *
   * @param joinPoint the join point representing the method execution
   * @return the result of the method execution
   * @throws Throwable if the method execution throws an exception
   */
  @Around("within(com.example.sb.controllers..*) || "
      + "within(com.example.sb.exceptions..*)")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    String methodName = joinPoint.getSignature().toShortString();
    Object[] args = joinPoint.getArgs();
    String packageName = joinPoint.getSignature().getDeclaringType().getPackage().getName();

    if (packageName.contains("exceptions")) {
      String exceptionMessage = extractExceptionMessage(args);
      if (methodName.contains("handleEntityNotFoundException")
          || methodName.contains("handleIllegalArgumentException")) {
        log.warn("Handling exception in method: {} with message: {}",
            methodName, exceptionMessage);
      } else if (methodName.contains("handleGenericException")) {
        log.error("Handling exception in method: {} with message: {}",
            methodName, exceptionMessage);
      }
    } else {
      log.info("Entering method: {} with arguments: {}", methodName, Arrays.toString(args));
    }

    long startTime = System.currentTimeMillis();
    try {
      Object result = joinPoint.proceed();
      long executionTime = System.currentTimeMillis() - startTime;

      if (!packageName.contains("exceptions")) {
        log.info("Exiting method: {} with result: {} (execution time: {}ms)",
            methodName, result, executionTime);
      }

      return result;
    } catch (Throwable throwable) {
      // Re-throw the exception after logging
      throw throwable;
    }
  }

  /**
   * Logs exceptions thrown from controller methods.
   *
   * @param joinPoint the join point where the exception was thrown
   * @param ex the exception that was thrown
   */
  @AfterThrowing(
      pointcut = "within(com.example.sb.controllers..*)",
      throwing = "ex"
  )
  public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
    String methodName = joinPoint.getSignature().toShortString();
    if (ex instanceof EntityNotFoundException || ex instanceof IllegalArgumentException) {
      log.warn("Exception in method: {} with message: {}", methodName, ex.getMessage());
    } else {
      log.error("Exception in method: {} with message: {}", methodName, ex.getMessage());
    }
  }

  private String extractExceptionMessage(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof Throwable) {
        return ((Throwable) arg).getMessage();
      }
    }
    return "Unknown exception message";
  }
}