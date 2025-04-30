package com.example.sb.controllers;

import com.example.sb.cache.CarCache;
import com.example.sb.schemas.CarDTO;
import com.example.sb.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing car operations associated with users.
 */
@Validated
@RestController
@RequestMapping("/api/users/{userId}/cars")
@Tag(name = "Car Management", description = "Operations related to user cars")
public class CarController {

  private final CarService carService;
  private final CarCache carCache;

  /**
   * Constructs a CarController with the specified services.
   *
   * @param carService the car service
   * @param carCache the car cache
   */
  @Autowired
  public CarController(CarService carService, CarCache carCache) {
    this.carService = carService;
    this.carCache = carCache;
  }

  @Operation(
      summary = "Create multiple cars in bulk",
      description = "Creates multiple cars for the specified user in a single operation"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Cars created successfully",
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  @PostMapping("/bulk")
  public ResponseEntity<List<CarDTO>> createCarsBulk(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "List of car details to create",
          required = true,
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      )
      @Valid @RequestBody List<CarDTO> carDtos,
      @Parameter(description = "ID of the user") @PathVariable Long userId) {

    List<CarDTO> createdCars = carDtos.stream()
        .map(carDto -> carService.createCar(carDto, userId))
        .toList();

    return ResponseEntity.ok(createdCars);
  }

  /**
   * Creates a new car for the specified user.
   *
   * @param carDto the car data to create
   * @param userId the ID of the user
   * @return the created car
   */
  @Operation(
      summary = "Create a new car for user",
      description = "Creates a new car associated with the specified user"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Car created successfully",
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  @PostMapping
  public ResponseEntity<CarDTO> createCar(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Car details to create",
          required = true,
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      )
      @Valid @RequestBody CarDTO carDto,
      @Parameter(description = "ID of the user") @PathVariable Long userId) {
    return ResponseEntity.ok(carService.createCar(carDto, userId));
  }

  /**
   * Retrieves all cars for the specified user.
   *
   * @param userId the ID of the user
   * @return list of cars
   */
  @Operation(
      summary = "Get all user cars",
      description = "Retrieves all cars associated with the specified user"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved cars",
      content = @Content(schema = @Schema(implementation = CarDTO.class))
  )
  @GetMapping
  public ResponseEntity<List<CarDTO>> getAllCars(
      @Parameter(
          description = "ID of the user",
          required = true,
          example = "1"
      ) @PathVariable Long userId) {
    return ResponseEntity.ok(carService.getAllCars(userId));
  }

  /**
   * Retrieves a specific car by ID for the specified user.
   *
   * @param carId the ID of the car
   * @param userId the ID of the user
   * @return the car if found
   */
  @Operation(
      summary = "Get car by ID",
      description = "Retrieves a specific car by ID for the specified user"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Car found",
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Car not found"
      )
  })
  @GetMapping("/{carId}")
  public ResponseEntity<CarDTO> getCarById(
      @Parameter(
          description = "ID of the car to retrieve",
          required = true,
          example = "1"
      ) @PathVariable Long carId,
      @Parameter(
          description = "ID of the user who owns the car",
          required = true,
          example = "1"
      ) @PathVariable Long userId) {
    Optional<CarDTO> car = carService.getCarById(carId, userId);
    return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing car.
   *
   * @param carId the ID of the car to update
   * @param userId the ID of the user who owns the car
   * @param carDto the updated car data
   * @return the updated car if successful
   */
  @Operation(
      summary = "Update car",
      description = "Updates the details of an existing car"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Car updated successfully",
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Car not found"
      )
  })
  @PutMapping("/{carId}")
  public ResponseEntity<CarDTO> updateCar(
      @Parameter(
          description = "ID of the car to update",
          required = true,
          example = "1"
      ) @PathVariable Long carId,
      @Parameter(
          description = "ID of the user who owns the car",
          required = true,
          example = "1"
      ) @PathVariable Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Updated car details",
          required = true,
          content = @Content(schema = @Schema(implementation = CarDTO.class))
      ) @RequestBody CarDTO carDto) {
    Optional<CarDTO> updatedCar = carService.updateCar(carId, userId, carDto);
    return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a car by ID for the specified user.
   *
   * @param carId the ID of the car to delete
   * @param userId the ID of the user who owns the car
   * @return response indicating success or failure
   */
  @Operation(
      summary = "Delete car",
      description = "Deletes a car by ID for the specified user"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Car deleted successfully"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "Car not found"
      )
  })
  @DeleteMapping("/{carId}")
  public ResponseEntity<Void> deleteCar(
      @Parameter(
          description = "ID of the car to delete",
          required = true,
          example = "1"
      ) @PathVariable Long carId,
      @Parameter(
          description = "ID of the user who owns the car",
          required = true,
          example = "1"
      ) @PathVariable Long userId) {
    if (carService.deleteCar(carId, userId)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Clears the car cache.
   *
   * @return response indicating success
   */
  @Operation(
      summary = "Clear car cache",
      description = "Clears all cached car data"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Cache cleared successfully"
  )
  @PostMapping("/cache/clear")
  public ResponseEntity<Void> clearCache() {
    carCache.clear();
    return ResponseEntity.ok().build();
  }

  /**
   * Retrieves cars for the specified user with optional filters.
   *
   * @param userId the ID of the user
   * @param name the car name filter
   * @param fuelType the fuel type filter
   * @param minYear the minimum manufacturing year
   * @param maxYear the maximum manufacturing year
   * @param minMileage the minimum mileage
   * @param maxMileage the maximum mileage
   * @return list of filtered cars
   */
  @Operation(
      summary = "Get cars with filters",
      description = "Retrieves cars for the specified user with optional filters"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved filtered cars",
      content = @Content(schema = @Schema(implementation = CarDTO.class))
  )
  @GetMapping("/filter")
  public ResponseEntity<List<CarDTO>> getCarsWithFilters(
      @Parameter(
          description = "ID of the user",
          required = true,
          example = "1"
      ) @PathVariable Long userId,
      @Parameter(
          description = "Car name filter",
          example = "Toyota"
      ) @Size(
          max = 50,
          message = "Car name must be less than 50 characters"
      ) @RequestParam(required = false) String name,
      @Parameter(
          description = "Fuel type filter (gasoline, diesel, electric, hybrid)",
          example = "gasoline"
      ) @Pattern(
          regexp = "^(petrol|diesel|electric|hybrid)?$",
          message = "Invalid fuel type"
      ) @RequestParam(required = false) String fuelType,
      @Parameter(
          description = "Minimum manufacturing year",
          example = "2000"
      ) @Min(
          value = 1900,
          message = "Year must be after 1900"
      ) @Max(
          value = 2100,
          message = "Year must be before 2100"
      ) @RequestParam(required = false) Integer minYear,
      @Parameter(
          description = "Maximum manufacturing year",
          example = "2023"
      ) @Min(
          value = 1900,
          message = "Year must be after 1900"
      ) @Max(
          value = 2100,
          message = "Year must be before 2100"
      ) @RequestParam(required = false) Integer maxYear,
      @Parameter(
          description = "Minimum mileage",
          example = "0"
      ) @Min(
          value = 0,
          message = "Mileage cannot be negative"
      ) @Max(
          value = 1000000,
          message = "Mileage must be less than 1,000,000"
      ) @RequestParam(required = false) Integer minMileage,
      @Parameter(
          description = "Maximum mileage",
          example = "100000"
      ) @Min(
          value = 0,
          message = "Mileage cannot be negative"
      ) @Max(
          value = 1000000,
          message = "Mileage must be less than 1,000,000"
      ) @RequestParam(required = false) Integer maxMileage) {
    List<CarDTO> cars = carService.getCarsWithFilters(
        userId, name, fuelType, minYear, maxYear, minMileage, maxMileage);
    return ResponseEntity.ok(cars);
  }
}