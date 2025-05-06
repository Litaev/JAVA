package com.example.sb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SpringBootApp.
 */
@RestController
@SpringBootApplication
public class SbApplication {
  public static void main(String[] args) {
    SpringApplication.run(SbApplication.class, args);
  }

  @GetMapping("/")
  public String home() {
    return "Application is running!";
  }
}
