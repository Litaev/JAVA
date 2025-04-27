package com.example.sb.service;

import com.example.sb.cache.CarCache;
import com.example.sb.models.Car;
import com.example.sb.models.User;
import com.example.sb.repository.CarRepository;
import com.example.sb.schemas.CarDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с автомобилями пользователей.
 */
@Service
public class CarService {

  private static final Logger logger = LoggerFactory.getLogger(CarService.class);

  private final CarRepository carRepository;
  private final CarCache carCache;

  /**
   * Конструктор для CarService.
   *
   * @param carRepository репозиторий для работы с автомобилями.
   * @param carCache кэш для автомобилей.
   */
  @Autowired
  public CarService(CarRepository carRepository, CarCache carCache) {
    this.carRepository = carRepository;
    this.carCache = carCache;
  }

  /**
   * Создание нового автомобиля для пользователя.
   *
   * @param carDto данные автомобиля.
   * @param userId идентификатор пользователя.
   * @return объект CarDTO, содержащий информацию о созданном автомобиле.
   */
  @Transactional
  public CarDTO createCar(CarDTO carDto, Long userId) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.

    User user = new User();
    user.setId(userId);
    Car car = carDto.toEntity();
    car.setOwner(user);

    Car savedCar = carRepository.save(car);
    invalidateUserCache(userId);

    logger.info(
        "Created car ID: {} for user ID: {} in {}ms",
        savedCar.getId(), userId, System.currentTimeMillis() - startTime
    );

    return CarDTO.fromEntity(savedCar);
  }

  /**
   * Получение всех автомобилей пользователя.
   *
   * @param userId идентификатор пользователя.
   * @return список объектов CarDTO для пользователя.
   */
  @Transactional(readOnly = true)
  public List<CarDTO> getAllCars(Long userId) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.
    String cacheKey = getCacheKey(userId);

    logger.debug("Attempting to get user {} cars from cache", userId);
    Optional<List<Car>> cachedCars = carCache.get(cacheKey);

    if (cachedCars.isPresent()) {
      logger.debug(
          "Cache found for user {}, number of cars: {}",
          userId, cachedCars.get().size()
      );
      return convertToDtoList(cachedCars.get());
    }

    logger.debug("Cache not found for user {}, querying the database", userId);
    List<Car> cars = carRepository.findByOwnerId(userId);
    carCache.put(cacheKey, cars);

    logger.info(
        "Fetched {} cars from DB for user {} in {}ms",
        cars.size(), userId, System.currentTimeMillis() - startTime
    );

    return convertToDtoList(cars);
  }

  /**
   * Получение автомобилей пользователя с фильтрами.
   *
   * @param userId    идентификатор пользователя.
   * @param name      название автомобиля.
   * @param fuelType  тип топлива.
   * @param minYear   минимальный год выпуска.
   * @param maxYear   максимальный год выпуска.
   * @param minMileage минимальный пробег.
   * @param maxMileage максимальный пробег.
   * @return список объектов CarDTO для пользователя с применёнными фильтрами.
   */
  @Transactional(readOnly = true)
  public List<CarDTO> getCarsWithFilters(
      Long userId,
      String name,
      String fuelType,
      Integer minYear,
      Integer maxYear,
      Integer minMileage,
      Integer maxMileage
  ) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.
    String cacheKey = String.format(
        "%s_filter_%s_%s_%s_%s_%s_%s",
        getCacheKey(userId), name, fuelType, minYear, maxYear, minMileage, maxMileage
    );

    logger.debug(
        "Attempting to get filtered cars for user {} from cache with key {}",
        userId, cacheKey
    );

    Optional<List<Car>> cachedCars = carCache.get(cacheKey);
    if (cachedCars.isPresent()) {
      logger.debug(
          "Cache found for filtered cars, key: {}, number of cars: {}",
          cacheKey, cachedCars.get().size()
      );
      return convertToDtoList(cachedCars.get());
    }

    logger.debug("Cache not found, performing query in the database");
    List<Car> cars = carRepository.findByOwnerIdWithFilters(
        userId, name, fuelType, minYear, maxYear, minMileage, maxMileage
    );
    carCache.put(cacheKey, cars);

    logger.info(
        "Query with filters executed for user {} in {}ms. Found {} cars. Filters: "
            + "name={}, fuelType={}, year={}-{}, mileage={}-{}",
        userId, System.currentTimeMillis() - startTime, cars.size(),
        name, fuelType, minYear, maxYear, minMileage, maxMileage
    );

    return convertToDtoList(cars);
  }

  /**
   * Получение автомобиля по ID и ID пользователя.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @return объект CarDTO, если автомобиль найден, иначе Optional.empty().
   */
  @Transactional(readOnly = true)
  public Optional<CarDTO> getCarById(Long carId, Long userId) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.

    Optional<Car> car = carRepository.findByIdAndOwnerId(carId, userId);
    if (car.isPresent()) {
      logger.debug(
          "Found car ID: {} for user {} in {}ms",
          carId, userId, System.currentTimeMillis() - startTime
      );
      return car.map(CarDTO::fromEntity);
    }

    logger.debug("Car ID: {} not found for user {}", carId, userId);
    return Optional.empty();
  }

  /**
   * Обновление данных автомобиля пользователя.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @param carDto данные автомобиля для обновления.
   * @return объект CarDTO с обновлёнными данными, если автомобиль найден, иначе Optional.empty().
   */
  @Transactional
  public Optional<CarDTO> updateCar(Long carId, Long userId, CarDTO carDto) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.

    if (!carRepository.existsByIdAndOwnerId(carId, userId)) {
      logger.warn("Update not possible — car ID: {} not found for user {}", carId, userId);
      return Optional.empty();
    }

    Car car = carDto.toEntity();
    car.setId(carId);
    User owner = new User();
    owner.setId(userId);
    car.setOwner(owner);

    Car updatedCar = carRepository.save(car);
    invalidateUserCache(userId);

    logger.info(
        "Updated car ID: {} for user {} in {}ms",
        carId, userId, System.currentTimeMillis() - startTime
    );

    return Optional.of(CarDTO.fromEntity(updatedCar));
  }

  /**
   * Удаление автомобиля пользователя.
   *
   * @param carId идентификатор автомобиля.
   * @param userId идентификатор пользователя.
   * @return true, если автомобиль был успешно удалён, иначе false.
   */
  @Transactional
  public boolean deleteCar(Long carId, Long userId) {
    final long startTime = System.currentTimeMillis(); // Объявление сразу перед использованием.

    if (!carRepository.existsByIdAndOwnerId(carId, userId)) {
      logger.warn("Deletion not possible — car ID: {} not found for user {}", carId, userId);
      return false;
    }

    carRepository.deleteById(carId);
    invalidateUserCache(userId);

    logger.info(
        "Deleted car ID: {} for user {} in {}ms",
        carId, userId, System.currentTimeMillis() - startTime
    );

    return true;
  }

  /**
   * Очистка кэша машин одного пользователя.
   *
   * @param userId идентификатор пользователя.
   */
  public void clearUserCache(Long userId) {
    carCache.evict(getCacheKey(userId));
    logger.info("Cleared cache for user ID: {}", userId);
  }

  /**
   * Полная очистка кэша всех машин.
   */
  public void clearAllCache() {
    carCache.clear();
    logger.info("Completely cleared car cache");
  }

  private List<CarDTO> convertToDtoList(List<Car> cars) {
    return cars.stream()
        .map(CarDTO::fromEntity)
        .toList();
  }

  private String getCacheKey(Long userId) {
    return "user_" + userId + "_cars";
  }

  private void invalidateUserCache(Long userId) {
    carCache.evict(getCacheKey(userId));
    logger.debug("Cache for user ID: {} invalidated", userId);
  }
}
