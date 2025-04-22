package com.example.sb.service;

import com.example.sb.models.Car;
import com.example.sb.models.User;
import com.example.sb.repository.CarRepository;
import com.example.sb.schemas.CarDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling car-related business logic.
 */
@Service
public class CarService {

  private final CarRepository carRepository;
  private final Map<Long, List<Car>> cache = new ConcurrentHashMap<>();

  /**
   * Constructs a {@code CarService} instance.
   *
   * @param carRepository the repository for {@link Car} entities
   */
  @Autowired
  public CarService(CarRepository carRepository) {
    this.carRepository = carRepository;
  }

  /**
   * Creates a new car for the specified user.
   *
   * @param carDto the DTO containing car details
   * @param userId the ID of the user to associate the car with
   * @return the created car DTO
   */
  public CarDTO createCar(CarDTO carDto, Long userId) {
    User user = new User();
    user.setId(userId);
    Car car = carDto.toEntity();
    car.setOwner(user);
    Car savedCar = carRepository.save(car);
    return CarDTO.fromEntity(savedCar);
  }

  /**
   * Retrieves all cars belonging to the specified user.
   *
   * @param userId the ID of the user whose cars to retrieve
   * @return a list of car DTOs
   */
  public List<CarDTO> getAllCars(Long userId) {
    List<Car> cars = cache.computeIfAbsent(userId, id -> carRepository.findByOwnerId(userId));
    List<CarDTO> listCarDto = new ArrayList<>();
    for (Car car : cars) {
      listCarDto.add(CarDTO.fromEntity(car));
    }
    return listCarDto;
  }

  /**
   * Retrieves a car by its ID and associated user ID.
   *
   * @param carId the ID of the car to retrieve
   * @param userId the ID of the user associated with the car
   * @return an optional car DTO
   */
  public Optional<CarDTO> getCarById(Long carId, Long userId) {
    Optional<Car> car = carRepository.findByIdAndOwnerId(carId, userId);
    return car.map(CarDTO::fromEntity);
  }

  /**
   * Updates an existing car with the provided details.
   *
   * @param carId the ID of the car to update
   * @param userId the ID of the user associated with the car
   * @param carDto the DTO containing updated car details
   * @return an optional updated car DTO
   */
  public Optional<CarDTO> updateCar(Long carId, Long userId, CarDTO carDto) {
    if (carRepository.existsById(carId)) {
      Car car = carDto.toEntity();
      User user = new User();
      user.setId(userId);
      car.setId(carId);
      car.setOwner(user);
      Car updatedCar = carRepository.save(car);
      return Optional.of(CarDTO.fromEntity(updatedCar));
    }
    return Optional.empty();
  }

  /**
   * Deletes a car by its ID.
   *
   * @param carId the ID of the car to delete
   * @return true if the car was deleted, false otherwise
   */
  public boolean deleteCar(Long carId) {
    if (carRepository.existsById(carId)) {
      carRepository.deleteById(carId);
      return true;
    }
    return false;
  }
}
