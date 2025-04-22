package com.example.sb.schemas;

import com.example.sb.models.Car;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for the {@link Car} entity.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarDTO {

  private final Long id;
  private final String name;
  private final String fuelType;
  private final Integer year;
  private final Integer tankVolume;
  private final Integer mileage;

  /**
   * Constructs a {@code CarDTO} instance with all fields.
   *
   * @param id the car ID
   * @param name the car name
   * @param fuelType the type of fuel
   * @param year the manufacturing year
   * @param tankVolume the volume of the fuel tank
   * @param mileage the car mileage
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
   * Maps a {@link Car} entity to a {@code CarDTO}.
   *
   * @param car the entity to convert
   * @return the DTO instance
   */
  public static CarDTO fromEntity(Car car) {
    return new CarDTO(
        car.getId(),
        car.getName(),
        car.getFuelType(),
        car.getYear(),
        car.getTankVolume(),
        car.getMileage());
  }

  /**
   * Maps this DTO back to a {@link Car} entity.
   *
   * @return the entity instance
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
