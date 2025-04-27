package com.example.sb.service;

import com.example.sb.cache.UserCache;
import com.example.sb.models.User;
import com.example.sb.repository.UserRepository;
import com.example.sb.schemas.UserDTO;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями.
 */
@Service
public class UserService {

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final UserCache userCache;

  /**
   * Конструктор для UserService.
   *
   * @param userRepository репозиторий для работы с пользователями.
   * @param userCache кэш для пользователей.
   */
  @Autowired
  public UserService(UserRepository userRepository, UserCache userCache) {
    this.userRepository = userRepository;
    this.userCache = userCache;
  }

  /**
   * Создание нового пользователя.
   *
   * @param userDto данные пользователя.
   * @return объект UserDTO, содержащий информацию о созданном пользователе.
   */
  public UserDTO createUser(UserDTO userDto) {
    User user = userDto.toEntity();
    return UserDTO.fromEntity(userRepository.save(user));
  }

  /**
   * Получение всех пользователей.
   *
   * @return список объектов UserDTO для всех пользователей.
   */
  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(UserDTO::fromEntity)
        .toList();
  }

  /**
   * Получение пользователя по ID.
   *
   * @param id идентификатор пользователя.
   * @return объект UserDTO, если пользователь найден, иначе Optional.empty().
   */
  public Optional<UserDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserDTO::fromEntity);
  }

  /**
   * Обновление данных пользователя.
   *
   * @param id идентификатор пользователя.
   * @param userDto данные пользователя для обновления.
   * @return объект UserDTO с обновленными данными
   */
  public Optional<UserDTO> updateUser(Long id, UserDTO userDto) {
    if (userRepository.existsById(id)) {
      User updatedUser = userDto.toEntity();
      updatedUser.setId(id);
      return Optional.of(UserDTO.fromEntity(userRepository.save(updatedUser)));
    }
    return Optional.empty();
  }

  /**
   * Удаление пользователя.
   *
   * @param id идентификатор пользователя.
   * @return true, если пользователь был успешно удалён, иначе false.
   */
  public boolean deleteUser(Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Получение пользователей с фильтрами по автомобилям.
   *
   * @param minYear минимальный год выпуска.
   * @param maxYear максимальный год выпуска.
   * @param minMileage минимальный пробег.
   * @param maxMileage максимальный пробег.
   * @param fuelType тип топлива.
   * @param carName название автомобиля.
   * @return список объектов UserDTO для пользователей с фильтрами по автомобилям.
   */
  public List<UserDTO> getUsersWithCarFilters(
      Integer minYear,
      Integer maxYear,
      Integer minMileage,
      Integer maxMileage,
      String fuelType,
      String carName) {
    String cacheKey = buildCacheKey(minYear, maxYear, minMileage, maxMileage, fuelType, carName);
    Optional<List<User>> cachedUsers = userCache.get(cacheKey);
    final long startTime = System.currentTimeMillis();
    if (cachedUsers.isPresent()) {
      logger.debug("Returning cached users with filters");
      return convertToDtoList(cachedUsers.get());
    }

    logger.debug("Cache not found, querying users with filters from database");
    List<User> users = userRepository.findUsersWithCarFilters(
        minYear, maxYear, minMileage, maxMileage, fuelType, carName);

    userCache.put(cacheKey, users);


    return convertToDtoList(users);
  }

  /**
   * Построение ключа для кэша с фильтрами.
   *
   * @param minYear минимальный год выпуска.
   * @param maxYear максимальный год выпуска.
   * @param minMileage минимальный пробег.
   * @param maxMileage максимальный пробег.
   * @param fuelType тип топлива.
   * @param carName название автомобиля.
   * @return строка, представляющая ключ для кэша.
   */
  private String buildCacheKey(Integer minYear, Integer maxYear,
                               Integer minMileage, Integer maxMileage,
                               String fuelType, String carName) {
    return String.format("user_filter_%s_%s_%s_%s_%s_%s",
        minYear != null ? minYear : "null",
        maxYear != null ? maxYear : "null",
        minMileage != null ? minMileage : "null",
        maxMileage != null ? maxMileage : "null",
        fuelType != null ? fuelType : "null",
        carName != null ? carName : "null");
  }

  /**
   * Преобразование списка пользователей в список UserDTO.
   *
   * @param users список пользователей.
   * @return список объектов UserDTO.
   */
  private List<UserDTO> convertToDtoList(List<User> users) {
    return users.stream()
        .map(UserDTO::fromEntity)
        .toList();
  }
}
