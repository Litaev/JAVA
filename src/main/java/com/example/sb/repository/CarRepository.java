package com.example.sb.repository;

import com.example.sb.models.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для работы с сущностью {@link Car}.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

  /**
   * Находит машину по её id и id владельца.
   *
   * @param id идентификатор машины.
   * @param ownerId идентификатор владельца.
   * @return Опционально найденная машина.
   */
  Optional<Car> findByIdAndOwnerId(Long id, Long ownerId);

  /**
   * Находит все машины, принадлежащие пользователю по его id.
   *
   * @param userId идентификатор пользователя.
   * @return список машин пользователя.
   */
  @Query("SELECT c FROM Car c WHERE c.owner.id = :userId")
  List<Car> findByOwnerId(@Param("userId") Long userId);

  /**
   * Находит машины пользователя с дополнительной фильтрацией по атрибутам.
   *
   * @param userId идентификатор пользователя.
   * @param name часть названия машины.
   * @param fuelType тип топлива.
   * @param minYear минимальный год выпуска.
   * @param maxYear максимальный год выпуска.
   * @param minMileage минимальный пробег.
   * @param maxMileage максимальный пробег.
   * @return список машин, удовлетворяющих фильтрам.
   */
  @Query(value = "SELECT * FROM cars WHERE owner_id = :userId "
      + "AND (:name IS NULL OR name LIKE CONCAT('%', :name, '%')) "
      + "AND (:fuelType IS NULL OR fuel_type = :fuelType) "
      + "AND (:minYear IS NULL OR year >= :minYear) "
      + "AND (:maxYear IS NULL OR year <= :maxYear) "
      + "AND (:minMileage IS NULL OR mileage >= :minMileage) "
      + "AND (:maxMileage IS NULL OR mileage <= :maxMileage)",
      nativeQuery = true)
  List<Car> findByOwnerIdWithFilters(
      @Param("userId") Long userId,
      @Param("name") String name,
      @Param("fuelType") String fuelType,
      @Param("minYear") Integer minYear,
      @Param("maxYear") Integer maxYear,
      @Param("minMileage") Integer minMileage,
      @Param("maxMileage") Integer maxMileage);

  /**
   * Проверяет наличие машины по id и id владельца.
   *
   * @param id идентификатор машины.
   * @param ownerId идентификатор владельца.
   * @return true, если машина найдена, иначе false.
   */
  boolean existsByIdAndOwnerId(Long id, Long ownerId);
}
