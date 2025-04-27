package com.example.sb.controllers;

import com.example.sb.cache.UserCache;
import com.example.sb.schemas.UserDTO;
import com.example.sb.service.UserService;
import java.util.List;
import java.util.Map;
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
 * Контроллер для обработки запросов, связанных с пользователями.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;
  private final UserCache userCache;

  /**
   * Конструктор для инициализации контроллера с зависимостями.
   *
   * @param userService сервис для работы с пользователями.
   * @param userCache кэш пользователей.
   */
  @Autowired
  public UserController(UserService userService, UserCache userCache) {
    this.userService = userService;
    this.userCache = userCache;
  }

  /**
   * Получение пользователей с фильтрами по автомобилям.
   *
   * @param carMinYear минимальный год автомобиля.
   * @param carMaxYear максимальный год автомобиля.
   * @param carMinMileage минимальный пробег.
   * @param carMaxMileage максимальный пробег.
   * @param carFuelType тип топлива автомобиля.
   * @param carName название автомобиля.
   * @return список пользователей с фильтрами по автомобилям.
   */
  @GetMapping("/info")
  public ResponseEntity<List<UserDTO>> getUsersWithCarFilters(
      @RequestParam(required = false) Integer carMinYear,
      @RequestParam(required = false) Integer carMaxYear,
      @RequestParam(required = false) Integer carMinMileage,
      @RequestParam(required = false) Integer carMaxMileage,
      @RequestParam(required = false) String carFuelType,
      @RequestParam(required = false) String carName) {

    return ResponseEntity.ok(userService.getUsersWithCarFilters(
        carMinYear, carMaxYear,
        carMinMileage, carMaxMileage,
        carFuelType, carName));
  }

  /**
   * Создание нового пользователя.
   *
   * @param userDto данные нового пользователя.
   * @return созданный пользователь.
   */
  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
    UserDTO createdUser = userService.createUser(userDto);
    return ResponseEntity.ok(createdUser);
  }

  /**
   * Получение всех пользователей.
   *
   * @return список всех пользователей.
   */
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Получение информации о пользователе по его идентификатору.
   *
   * @param userId идентификатор пользователя.
   * @return найденный пользователь или ошибка 404.
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    Optional<UserDTO> user = userService.getUserById(userId);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Обновление данных пользователя.
   *
   * @param userId идентификатор пользователя.
   * @param userDto обновленные данные пользователя.
   * @return обновленный пользователь или ошибка 404.
   */
  @PutMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable Long userId,
      @RequestBody UserDTO userDto) {
    Optional<UserDTO> updatedUser = userService.updateUser(userId, userDto);
    return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Удаление пользователя по его идентификатору.
   *
   * @param userId идентификатор пользователя.
   * @return статус операции.
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    boolean isDeleted = userService.deleteUser(userId);
    if (isDeleted) {
      return ResponseEntity.ok().build();
    }
    return ResponseEntity.notFound().build();
  }

  /**
   * Очистка кэша пользователей.
   *
   * @return статус операции.
   */
  @PostMapping("/cache/clear")
  public ResponseEntity<Void> clearCache() {
    userCache.clear();
    return ResponseEntity.ok().build();
  }

  /**
   * Получение статистики о кэше пользователей.
   *
   * @return статистика кэша.
   */
  @GetMapping("/cache/stats")
  public ResponseEntity<Map<String, Object>> getCacheStats() {
    return ResponseEntity.ok(userCache.getCacheStats());
  }
}
