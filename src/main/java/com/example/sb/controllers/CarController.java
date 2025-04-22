package com.example.sb.controllers;

import com.example.sb.schemas.CarDTO;
import com.example.sb.service.CarService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling car-related operations for a specific user.
 */
@RestController
@RequestMapping("/api/users/{userId}/cars")
public class CarController {

  private final CarService carService;

  /**
   * Constructs a new {@code CarController} with the given car service.
   *
   * @param carService the car service
   */
  @Autowired
  public CarController(CarService carService) {
    this.carService = carService;
  }

  /**
   * Creates a new car for the specified user.
   *
   * @param carDto the car data
   * @param userId the ID of the user
   * @return the created car
   */
  @PostMapping
  public ResponseEntity<CarDTO> createCar(
      @RequestBody CarDTO carDto, @PathVariable Long userId) {
    CarDTO createdCar = carService.createCar(carDto, userId);
    return ResponseEntity.ok(createdCar);
  }

  /**
   * Returns all cars for the specified user.
   *
   * @param userId the ID of the user
   * @return list of cars
   */
  @GetMapping
  public ResponseEntity<List<CarDTO>> getAllCars(@PathVariable Long userId) {
    List<CarDTO> cars = carService.getAllCars(userId);
    return ResponseEntity.ok(cars);
  }

  /**
   * Returns a specific car by ID for the specified user.
   *
   * @param carId the ID of the car
   * @param userId the ID of the user
   * @return the car if found, otherwise 404
   */
  @GetMapping("/{carId}")
  public ResponseEntity<CarDTO> getCarById(
      @PathVariable Long carId, @PathVariable Long userId) {
    Optional<CarDTO> car = carService.getCarById(carId, userId);
    return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing car for the specified user.
   *
   * @param carId the ID of the car
   * @param userId the ID of the user
   * @param carDto the updated car data
   * @return the updated car if found, otherwise 404
   */
  @PutMapping("/{carId}")
  public ResponseEntity<CarDTO> updateCar(
      @PathVariable Long carId,
      @PathVariable Long userId,
      @RequestBody CarDTO carDto) {
    Optional<CarDTO> updatedCar = carService.updateCar(carId, userId, carDto);
    return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a car by ID.
   *
   * @param carId the ID of the car
   * @param userId the ID of the user
   * @return 200 OK if deleted, otherwise 404
   */
  @DeleteMapping("/{carId}")
  public ResponseEntity<CarDTO> deleteCar(
      @PathVariable Long carId, @PathVariable Long userId) {
    boolean isDeleted = carService.deleteCar(carId);
    if (isDeleted) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
