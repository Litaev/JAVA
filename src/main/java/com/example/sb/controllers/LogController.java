package com.example.sb.controllers;

import com.example.sb.exceptions.EntityNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing and retrieving application logs.
 */
@Slf4j
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Logs", description = "API for retrieving and downloading application logs")
public class LogController {

  private static final String LOG_PATH = "logs";
  private static final String LOG_FILE_PREFIX = "app-";
  private static final String LOG_FILE_EXTENSION = ".log";
  private static final String MAIN_LOG_FILE = LOG_PATH + "/app.log";

  private static final String LOG_CONTROLLER_PREFIX = "c.example.sb.controllers.LogController";

  /**
   * Retrieves logs for a specific date.
   *
   * @param date the date to retrieve logs for (ISO format)
   * @return list of log entries
   */
  @Operation(
      summary = "Get logs by date",
      description = "Retrieves application logs for the specified date"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Logs retrieved successfully",
          content = @Content(schema = @Schema(implementation = String.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No logs found for the specified date"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @GetMapping
  public ResponseEntity<List<String>> getLogsByDate(
      @Parameter(
          description = "Date in ISO format (YYYY-MM-DD)",
          required = true,
          example = "2023-01-01"
      ) @RequestParam("date") String date) {
    try {
      LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
      List<String> logEntries = getLogEntriesForDate(logDate);

      List<String> filteredLogs = logEntries.stream()
          .filter(line -> !line.contains(LOG_CONTROLLER_PREFIX))
          .collect(Collectors.toList());

      if (filteredLogs.isEmpty()) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(List.of("No log entries found for date: " + date));
      }

      return ResponseEntity.ok(filteredLogs);
    } catch (Exception ex) {
      log.error("Error retrieving logs", ex);
      return ResponseEntity.internalServerError()
          .body(List.of("Error: " + ex.getMessage()));
    }
  }

  /**
   * Downloads log file for a specific date.
   *
   * @param date the date to download logs for (ISO format)
   * @return downloadable log file resource
   */
  @Operation(
      summary = "Download logs",
      description = "Downloads application logs for the specified date as a file"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Log file downloaded successfully",
          content = @Content(schema = @Schema(implementation = Resource.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "No logs found for the specified date"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @GetMapping("/download")
  public ResponseEntity<Resource> downloadLogFile(
      @Parameter(
          description = "Date in ISO format (YYYY-MM-DD)",
          required = true,
          example = "2023-01-01"
      ) @RequestParam("date") String date) {
    try {
      LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
      List<String> logEntries = getLogEntriesForDate(logDate);

      List<String> filteredLogs = logEntries.stream()
          .filter(line -> !line.contains(LOG_CONTROLLER_PREFIX))
          .toList();

      if (filteredLogs.isEmpty()) {
        throw new EntityNotFoundException("No log entries found");
      }

      String timestamp = LocalDateTime.now()
          .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
      String fileName = String.format("%s/%s%s_%s%s",
          LOG_PATH, LOG_FILE_PREFIX, logDate, timestamp, LOG_FILE_EXTENSION);

      File logFile = new File(fileName);
      logFile.getParentFile().mkdirs();

      try (FileWriter writer = new FileWriter(logFile)) {
        for (String entry : filteredLogs) {
          writer.write(entry + System.lineSeparator());
        }
      }

      String headerValue = String.format(
          "attachment; filename=\"logs_%s_%s.log\"", logDate, timestamp);

      return ResponseEntity.ok()
          .contentType(MediaType.APPLICATION_OCTET_STREAM)
          .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
          .body(new FileSystemResource(logFile));

    } catch (Exception ex) {
      log.error("Error downloading logs", ex);
      throw new RuntimeException("Download error: " + ex.getMessage());
    }
  }

  private List<String> getLogEntriesForDate(LocalDate logDate) throws IOException {
    List<String> entries = new ArrayList<>();
    String dateStr = logDate.toString();

    addEntriesFromFile(new File(MAIN_LOG_FILE), dateStr, entries);

    String rotatedFile = String.format("%s/%s%s%s",
        LOG_PATH, LOG_FILE_PREFIX, logDate, LOG_FILE_EXTENSION);
    addEntriesFromFile(new File(rotatedFile), dateStr, entries);

    return entries;
  }

  private void addEntriesFromFile(File file, String datePrefix, List<String> entries)
      throws IOException {
    if (!file.exists()) {
      return;
    }

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.startsWith(datePrefix)) {
          entries.add(line);
        }
      }
    }
  }
}