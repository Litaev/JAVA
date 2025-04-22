package com.example.sb.repository;

import com.example.sb.models.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Car} entities.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

  /**
   * Finds a car by its ID and owner's ID.
   *
   * @param id the ID of the car
   * @param ownerId the ID of the owner
   * @return an {@link Optional} containing the found car, if any
   */
  Optional<Car> findByIdAndOwnerId(Long id, Long ownerId);

  /**
   * Finds all cars belonging to a specific owner.
   *
   * @param userId the ID of the owner
   * @return a list of cars owned by the user
   */
  @Query("SELECT t FROM Car t WHERE t.owner.id = :userId")
  List<Car> findByOwnerId(@Param("userId") Long userId);
}
