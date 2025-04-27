package com.example.sb.repository;

import com.example.sb.models.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностями {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Поиск пользователей с фильтрацией по атрибутам их автомобилей.
   *
   * @param minYear минимальный год выпуска автомобиля
   * @param maxYear максимальный год выпуска автомобиля
   * @param minMileage минимальный пробег автомобиля
   * @param maxMileage максимальный пробег автомобиля
   * @param fuelType тип топлива автомобиля
   * @param carName часть названия автомобиля
   * @return список пользователей, соответствующих фильтрам
   */
  @Query(
      "SELECT DISTINCT u FROM User u JOIN FETCH u.cars c "
          + "WHERE (:minYear IS NULL OR c.year >= :minYear) "
          + "AND (:maxYear IS NULL OR c.year <= :maxYear) "
          + "AND (:minMileage IS NULL OR c.mileage >= :minMileage) "
          + "AND (:maxMileage IS NULL OR c.mileage <= :maxMileage) "
          + "AND (:fuelType IS NULL OR c.fuelType = :fuelType) "
          + "AND (:carName IS NULL OR c.name LIKE %:carName%)"
  )
  List<User> findUsersWithCarFilters(
      @Param("minYear") Integer minYear,
      @Param("maxYear") Integer maxYear,
      @Param("minMileage") Integer minMileage,
      @Param("maxMileage") Integer maxMileage,
      @Param("fuelType") String fuelType,
      @Param("carName") String carName
  );
}
