package com.example.sb.service;

import com.example.sb.cache.CarCache;
import com.example.sb.models.Car;
import com.example.sb.models.User;
import com.example.sb.repository.CarRepository;
import com.example.sb.schemas.CarDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

  @Mock
  private CarRepository carRepository;

  @Mock
  private CarCache carCache;

  @InjectMocks
  private CarService carService;

  private CarDTO testCarDto;
  private Car testCar;
  private User testUser;

  @BeforeEach
  void setUp() {
    testCarDto = new CarDTO(null, "Test Car", "petrol", 2020, 50, 10000);
    testCar = testCarDto.toEntity();
    testUser = new User();
    testUser.setId(1L);
    testCar.setOwner(testUser);
  }

  @Test
  void createCar_ShouldReturnCreatedCar() {
    when(carRepository.save(any(Car.class))).thenReturn(testCar);

    CarDTO result = carService.createCar(testCarDto, 1L);

    assertNotNull(result);
    assertEquals(testCarDto.getName(), result.getName());
    verify(carCache).evict(anyString());
    verify(carRepository).save(any(Car.class));
  }

  @Test
  void getAllCars_ShouldReturnCarsFromCache() {
    String cacheKey = "user_1_cars";
    when(carCache.get(cacheKey)).thenReturn(Optional.of(List.of(testCar)));

    List<CarDTO> result = carService.getAllCars(1L);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(carCache).get(cacheKey);
    verify(carRepository, never()).findByOwnerId(anyLong());
  }

  @Test
  void getAllCars_ShouldReturnCarsFromRepositoryWhenCacheEmpty() {
    String cacheKey = "user_1_cars";
    when(carCache.get(cacheKey)).thenReturn(Optional.empty());
    when(carRepository.findByOwnerId(1L)).thenReturn(List.of(testCar));

    List<CarDTO> result = carService.getAllCars(1L);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
    verify(carCache).put(cacheKey, List.of(testCar));
  }

  @Test
  void getCarById_ShouldReturnCarWhenExists() {
    when(carRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.of(testCar));

    Optional<CarDTO> result = carService.getCarById(1L, 1L);

    assertTrue(result.isPresent());
    assertEquals(testCarDto.getName(), result.get().getName());
  }

  @Test
  void getCarById_ShouldReturnEmptyWhenNotExists() {
    when(carRepository.findByIdAndOwnerId(1L, 1L)).thenReturn(Optional.empty());

    Optional<CarDTO> result = carService.getCarById(1L, 1L);

    assertTrue(result.isEmpty());
  }

  @Test
  void updateCar_ShouldUpdateWhenCarExists() {
    when(carRepository.existsByIdAndOwnerId(1L, 1L)).thenReturn(true);
    when(carRepository.save(any(Car.class))).thenReturn(testCar);

    Optional<CarDTO> result = carService.updateCar(1L, 1L, testCarDto);

    assertTrue(result.isPresent());
    assertEquals(testCarDto.getName(), result.get().getName());
    verify(carCache).evict(anyString());
  }

  @Test
  void updateCar_ShouldReturnEmptyWhenCarNotExists() {
    when(carRepository.existsByIdAndOwnerId(1L, 1L)).thenReturn(false);

    Optional<CarDTO> result = carService.updateCar(1L, 1L, testCarDto);

    assertTrue(result.isEmpty());
    verify(carRepository, never()).save(any());
  }

  @Test
  void deleteCar_ShouldReturnTrueWhenCarExists() {
    when(carRepository.existsByIdAndOwnerId(1L, 1L)).thenReturn(true);

    boolean result = carService.deleteCar(1L, 1L);

    assertTrue(result);
    verify(carRepository).deleteById(1L);
    verify(carCache).evict(anyString());
  }

  @Test
  void deleteCar_ShouldReturnFalseWhenCarNotExists() {
    when(carRepository.existsByIdAndOwnerId(1L, 1L)).thenReturn(false);

    boolean result = carService.deleteCar(1L, 1L);

    assertFalse(result);
    verify(carRepository, never()).deleteById(any());
  }

  @Test
  void getCarsWithFilters_ShouldReturnFilteredCars() {
    when(carRepository.findByOwnerIdWithFilters(
        anyLong(), any(), any(), any(), any(), any(), any()))
        .thenReturn(List.of(testCar));

    List<CarDTO> result = carService.getCarsWithFilters(
        1L, "Test", "petrol", 2010, 2023, 0, 50000);

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void clearAllCache_ShouldClearCache() {
    carService.clearAllCache();
    verify(carCache).clear();
  }
}