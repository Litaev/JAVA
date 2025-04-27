package com.example.sb.controllers;

import com.example.sb.cache.CarCache;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с автомобилями пользователя.
 * Обрабатывает запросы, связанные с CRUD операциями над автомобилями,
 * а также кэшированием данных об автомобилях.
 */
@RestController
@RequestMapping("/api/users/{userId}/cars")
public class CarController {

  private final CarService carService;
  private final CarCache carCache;

  /**
   * Конструктор контроллера для инъекции зависимостей.
   *
   * @param carService сервис для работы с автомобилями.
   * @param carCache кэш автомобилей.
   */
  @Autowired
  public CarController(CarService carService, CarCache carCache) {
    this.carService = carService;
    this.carCache = carCache;
  }

  /**
   * Создает новый автомобиль для пользователя.
   *
   * @param carDto данные автомобиля.
   * @param userId идентификатор пользователя.
   * @return созданный автомобиль.
   */
  @PostMapping
  public ResponseEntity<CarDTO> createCar(
      @RequestBody CarDTO carDto, @PathVariable Long userId) {
    return ResponseEntity.ok(carService.createCar(carDto, userId));
  }

  /**
   * Получает все автомобили пользователя.
   *
   * @param userId идентификатор пользователя.
   * @return список автомобилей.
   */
  @GetMapping
  public ResponseEntity<List<CarDTO>> getAllCars(@PathVariable Long userId) {
    return ResponseEntity.ok(carService.getAllCars(userId));
  }

  /**
   * Получает информацию об автомобиле по его идентификатору.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @return найденный автомобиль или ошибка 404.
   */
  @GetMapping("/{carId}")
  public ResponseEntity<CarDTO> getCarById(
      @PathVariable Long carId, @PathVariable Long userId) {
    Optional<CarDTO> car = carService.getCarById(carId, userId);
    return car.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Обновляет данные об автомобиле.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @param carDto обновленные данные автомобиля.
   * @return обновленный автомобиль или ошибка 404.
   */
  @PutMapping("/{carId}")
  public ResponseEntity<CarDTO> updateCar(
      @PathVariable Long carId,
      @PathVariable Long userId,
      @RequestBody CarDTO carDto) {
    Optional<CarDTO> updatedCar = carService.updateCar(carId, userId, carDto);
    return updatedCar.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Удаляет автомобиль.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @return статус операции.
   */
  @DeleteMapping("/{carId}")
  public ResponseEntity<Void> deleteCar(
      @PathVariable Long carId, @PathVariable Long userId) {
    if (carService.deleteCar(carId, userId)) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Очищает кэш автомобилей.
   *
   * @return статус операции.
   */
  @PostMapping("/cache/clear")
  public ResponseEntity<Void> clearCache() {
    carCache.clear();
    return ResponseEntity.ok().build();
  }

  /**
   * Получает автомобили с фильтрами.
   *
   * @param userId идентификатор пользователя.
   * @param name имя автомобиля (поиск по имени).
   * @param fuelType тип топлива.
   * @param minYear минимальный год автомобиля.
   * @param maxYear максимальный год автомобиля.
   * @param minMileage минимальный пробег.
   * @param maxMileage максимальный пробег.
   * @return список автомобилей, соответствующих фильтрам.
   */
  @GetMapping("/filter")
  public ResponseEntity<List<CarDTO>> getCarsWithFilters(
      @PathVariable Long userId,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String fuelType,
      @RequestParam(required = false) Integer minYear,
      @RequestParam(required = false) Integer maxYear,
      @RequestParam(required = false) Integer minMileage,
      @RequestParam(required = false) Integer maxMileage) {

    List<CarDTO> cars = carService.getCarsWithFilters(
        userId, name, fuelType, minYear, maxYear, minMileage, maxMileage);
    return ResponseEntity.ok(cars);
  }
}
