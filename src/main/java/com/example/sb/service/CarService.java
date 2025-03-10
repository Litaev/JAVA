package com.example.sb.service;


import com.example.sb.model.Car;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service CarService Class.
 */
@Service
public class CarService {
  private final Map<Integer, Car> carsDatabase = Map.of(
      1, new Car("VW Passat B5", "Diesel", 1, 2000, 0,
          62, 343021),
      2, new Car("BMW M5", "Petrol", 2, 2015, 0,
          73, 102312),
      3, new Car("Mazda 3", "Petrol", 3, 1998, 1,
          55, 412321)
  );

  /**
   * Get car by ID Function.
   */
  public Car getCarById(Integer carId) {
    if (!carsDatabase.containsKey(carId)) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Car not found");
    }
    return carsDatabase.get(carId);
  }

  /**
   * Get car by Name Function.
   */
  public Car getCarsByName(String carName) {
    return carsDatabase.values().stream()
        .filter(anime -> anime.getCarName().equalsIgnoreCase(carName))
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "Car not found"));
  }
}
