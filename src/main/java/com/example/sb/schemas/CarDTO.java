package com.example.sb.schemas;

import com.example.sb.models.Car;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for {@link Car} entity.
 *
 * <p>Contains validation constraints and conversion methods between DTO and entity.
 * Excludes null values during JSON serialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO {

  @Null(message = "ID should not be provided for creation")
  private final Long id;

  @NotBlank(message = "Car name is required")
  @Size(max = 50, message = "Car name must be less than 50 characters")
  private final String name;

  @NotBlank(message = "Fuel type is required")
  @Pattern(
      regexp = "^(petrol|diesel|electric|hybrid)$",
      message = "Fuel type must be one of: petrol, diesel, electric, hybrid"
  )
  private final String fuelType;

  @Min(value = 1900, message = "Year must be after 1900")
  @Max(value = 2100, message = "Year must be before 2100")
  private final Integer year;

  @Min(value = 1, message = "Tank volume must be at least 1")
  @Max(value = 200, message = "Tank volume must be less than 200")
  private final Integer tankVolume;

  @Min(value = 0, message = "Mileage cannot be negative")
  @Max(value = 1000000, message = "Mileage must be less than 1,000,000")
  private final Integer mileage;

  /**
   * Constructs a new CarDTO with specified parameters.
   *
   * @param id the car ID (should be null for creation)
   * @param name the car name (required, max 50 chars)
   * @param fuelType the fuel type (must be petrol/diesel/electric/hybrid)
   * @param year the manufacturing year (1900-2100)
   * @param tankVolume the tank volume (1-200)
   * @param mileage the car mileage (0-1,000,000)
   */
  public CarDTO(
      Long id,
      String name,
      String fuelType,
      Integer year,
      Integer tankVolume,
      Integer mileage) {
    this.id = id;
    this.name = name;
    this.fuelType = fuelType;
    this.year = year;
    this.tankVolume = tankVolume;
    this.mileage = mileage;
  }

  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  @JsonProperty("name")
  public String getName() {
    return name;
  }

  @JsonProperty("fuelType")
  public String getFuelType() {
    return fuelType;
  }

  @JsonProperty("year")
  public Integer getYear() {
    return year;
  }

  @JsonProperty("tankVolume")
  public Integer getTankVolume() {
    return tankVolume;
  }

  @JsonProperty("mileage")
  public Integer getMileage() {
    return mileage;
  }

  /**
   * Converts Car entity to CarDTO.
   *
   * @param car the entity to convert
   * @return new CarDTO instance
   */
  public static CarDTO fromEntity(Car car) {
    return new CarDTO(
        car.getId(),
        car.getName(),
        car.getFuelType(),
        car.getYear(),
        car.getTankVolume(),
        car.getMileage()
    );
  }

  /**
   * Converts this DTO to Car entity.
   *
   * @return new Car entity built from this DTO
   */
  public Car toEntity() {
    return Car.builder()
        .id(this.id)
        .name(this.name)
        .fuelType(this.fuelType)
        .year(this.year)
        .tankVolume(this.tankVolume)
        .mileage(this.mileage)
        .build();
  }
}