package com.example.sb.repository;

import com.example.sb.models.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link User} entity operations.
 *
 * <p>Provides methods to work with User entities including custom filtered queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * Finds users with filtering by their cars' attributes.
   *
   * <p>All parameters are optional - null values will be ignored in the query.
   * The query uses JOIN FETCH to avoid N+1 problem when accessing cars collection.
   *
   * @param nickName user nickname filter (exact match)
   * @param minYear minimum car manufacturing year (inclusive)
   * @param maxYear maximum car manufacturing year (inclusive)
   * @param minMileage minimum car mileage (inclusive)
   * @param maxMileage maximum car mileage (inclusive)
   * @param fuelType car fuel type (exact match)
   * @param carName part of car name (case-insensitive contains)
   * @return list of users matching all specified filters
   */
  @Query(
      "SELECT DISTINCT u FROM User u JOIN FETCH u.cars c "
          + "WHERE (:nickName IS NULL OR u.nickname = :nickName) "
          + "AND (:minYear IS NULL OR c.year >= :minYear) "
          + "AND (:maxYear IS NULL OR c.year <= :maxYear) "
          + "AND (:minMileage IS NULL OR c.mileage >= :minMileage) "
          + "AND (:maxMileage IS NULL OR c.mileage <= :maxMileage) "
          + "AND (:fuelType IS NULL OR c.fuelType = :fuelType) "
          + "AND (:carName IS NULL OR c.name LIKE %:carName%)"
  )
  List<User> findUsersWithCarFilters(
      @Param("nickName") String nickName,
      @Param("minYear") Integer minYear,
      @Param("maxYear") Integer maxYear,
      @Param("minMileage") Integer minMileage,
      @Param("maxMileage") Integer maxMileage,
      @Param("fuelType") String fuelType,
      @Param("carName") String carName
  );

  /**
   * Checks if a user with given email exists.
   *
   * @param email the email to check
   * @return true if email exists, false otherwise
   */
  boolean existsByEmail(String email);

  /**
   * Checks if a user with given nickname exists.
   *
   * @param nickname the nickname to check
   * @return true if nickname exists, false otherwise
   */
  boolean existsByNickname(String nickname);
}