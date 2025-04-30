package com.example.sb.controllers;

import com.example.sb.schemas.UserDTO;
import com.example.sb.service.UserService;
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
 * REST controller for managing user operations.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations related to users")
public class UserController {

  private final UserService userService;

  /**
   * Constructs a UserController with the specified service.
   *
   * @param userService the user service
   */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Retrieves users filtered by their car attributes.
   *
   * @param nickName the user nickname filter
   * @param carMinYear the minimum car manufacturing year
   * @param carMaxYear the maximum car manufacturing year
   * @param carMinMileage the minimum car mileage
   * @param carMaxMileage the maximum car mileage
   * @param carFuelType the car fuel type
   * @param carName the car name or part of name
   * @return list of filtered users
   */
  @Operation(
      summary = "Get users with car filters",
      description = "Retrieve users filtered by their car attributes"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "Successfully retrieved users",
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid parameters provided"
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error"
      )
  })
  @GetMapping("/info")
  public ResponseEntity<List<UserDTO>> getUsersWithCarFilters(
      @Parameter(description = "User nickname filter")
      @Size(max = 20, message = "Nickname must be less than 20 characters")
      @RequestParam(required = false) String nickName,
      @Parameter(
          description = "Minimum car manufacturing year",
          example = "2000"
      ) @Min(
          value = 1900,
          message = "Year must be after 1900"
      ) @Max(
          value = 2100,
          message = "Year must be before 2100"
      ) @RequestParam(required = false) Integer carMinYear,
      @Parameter(
          description = "Maximum car manufacturing year",
          example = "2023"
      ) @Min(
          value = 1900,
          message = "Year must be after 1900"
      ) @Max(
          value = 2100,
          message = "Year must be before 2100"
      ) @RequestParam(required = false) Integer carMaxYear,
      @Parameter(
          description = "Minimum car mileage",
          example = "0"
      ) @Min(
          value = 0,
          message = "Mileage cannot be negative"
      ) @Max(
          value = 1000000,
          message = "Mileage must be less than 1,000,000"
      ) @RequestParam(required = false) Integer carMinMileage,
      @Parameter(
          description = "Maximum car mileage",
          example = "100000"
      ) @Min(
          value = 0,
          message = "Mileage cannot be negative"
      ) @Max(
          value = 1000000,
          message = "Mileage must be less than 1,000,000"
      ) @RequestParam(required = false) Integer carMaxMileage,
      @Parameter(
          description = "Car fuel type (gasoline, diesel, electric, hybrid)",
          example = "gasoline"
      ) @Pattern(
          regexp = "^(gasoline|diesel|electric|hybrid)?$",
          message = "Invalid fuel type"
      ) @RequestParam(required = false) String carFuelType,
      @Parameter(
          description = "Car name or part of name",
          example = "Toyota"
      ) @Size(
          max = 50,
          message = "Car name must be less than 50 characters"
      ) @RequestParam(required = false) String carName) {
    return ResponseEntity.ok(userService.getUsersWithCarFilters(
        nickName,
        carMinYear, carMaxYear,
        carMinMileage, carMaxMileage,
        carFuelType, carName));
  }

  /**
   * Creates a new user.
   *
   * @param userDto the user details to create
   * @return the created user
   */
  @Operation(
      summary = "Create a new user",
      description = "Creates a new user with the provided details"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User created successfully",
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Invalid input data"
      )
  })
  @PostMapping
  public ResponseEntity<UserDTO> createUser(
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "User details to create",
          required = true,
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ) @Valid @RequestBody UserDTO userDto) {
    UserDTO createdUser = userService.createUser(userDto);
    return ResponseEntity.ok(createdUser);
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @Operation(
      summary = "Get all users",
      description = "Retrieves a list of all registered users"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Successfully retrieved users",
      content = @Content(schema = @Schema(implementation = UserDTO.class))
  )
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Retrieves a user by ID.
   *
   * @param userId the user ID
   * @return the user if found
   */
  @Operation(
      summary = "Get user by ID",
      description = "Retrieves a specific user by their unique identifier"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User found",
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(
      @Parameter(
          description = "ID of the user to retrieve",
          required = true,
          example = "1"
      ) @PathVariable Long userId) {
    Optional<UserDTO> user = userService.getUserById(userId);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing user.
   *
   * @param userId the user ID to update
   * @param userDto the updated user details
   * @return the updated user if successful
   */
  @Operation(
      summary = "Update user",
      description = "Updates the details of an existing user"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User updated successfully",
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  @PutMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUser(
      @Parameter(
          description = "ID of the user to update",
          required = true,
          example = "1"
      ) @PathVariable Long userId,
      @io.swagger.v3.oas.annotations.parameters.RequestBody(
          description = "Updated user details",
          required = true,
          content = @Content(schema = @Schema(implementation = UserDTO.class))
      ) @RequestBody UserDTO userDto) {
    Optional<UserDTO> updatedUser = userService.updateUser(userId, userDto);
    return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a user by ID.
   *
   * @param userId the user ID to delete
   * @return response indicating success or failure
   */
  @Operation(
      summary = "Delete user",
      description = "Deletes a user by their ID"
  )
  @ApiResponses({
      @ApiResponse(
          responseCode = "200",
          description = "User deleted successfully"
      ),
      @ApiResponse(
          responseCode = "404",
          description = "User not found"
      )
  })
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(
      @Parameter(
          description = "ID of the user to delete",
          required = true,
          example = "1"
      ) @PathVariable Long userId) {
    boolean isDeleted = userService.deleteUser(userId);
    if (isDeleted) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }
}