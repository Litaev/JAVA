package com.example.sb.controllers;

import com.example.sb.model.Car;
import com.example.sb.service.CarService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling car-related API requests.
 */
@RestController
@RequestMapping("/api")
public class CarController {

  private final CarService carService;

  /**
   * Controller class constructor.
   */
  public CarController(CarService carService)  {
    this.carService = carService;
  }

  /**
   * Retrieves a car info by its name.
   */
  @GetMapping("/cars")
  public Car getQueryCar(@RequestParam("name") String name) {
    return carService.getCarsByName(name);
  }

  /**
   * Retrieves a car info by its id..
   */
  @GetMapping("/cars/{carId}")
  public Car getPathCar(@PathVariable("carId") Integer carId) {
    return carService.getCarById(carId);
  }
}
