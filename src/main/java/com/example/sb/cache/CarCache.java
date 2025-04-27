package com.example.sb.cache;

import com.example.sb.models.Car;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Класс CarCache реализует кэш для хранения данных о автомобилях.
 * Кэш использует `ConcurrentHashMap` для хранения записей, поддерживает ограничение по размеру и
 * время жизни записей.
 * Периодически выполняется очистка устаревших записей.
 * Методы включают добавление, извлечение и удаление записей из кэша, а также статистику о текущем
 * состоянии кэша.
 */
@Component
public class CarCache {

  private static final Logger logger = LoggerFactory.getLogger(CarCache.class);

  private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
  private final AtomicInteger currentSize = new AtomicInteger(0);
  private static final int MAX_SIZE = 1000;
  private static final long DEFAULT_TTL = 30000; // 30 минут
  private final ScheduledExecutorService cleanupExecutor;

  /**
   * Конструктор класса CarCache, инициализирует ScheduledExecutorService.
   */
  public CarCache() {
    this.cleanupExecutor = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Метод инициализации, который запускает периодическую очистку кэша.
   */
  @PostConstruct
  public void init() {
    // Очистка каждые 30 секунд
    cleanupExecutor.scheduleAtFixedRate(
        this::cleanupExpiredEntries,
        30, 30, TimeUnit.SECONDS); // Изменили с 5 минут на 30 секунд
    logger.info("CarCache initialized with periodic cleanup every 30 seconds");
  }

  /**
   * Метод для уничтожения ресурсов перед завершением работы.
   */
  @PreDestroy
  public void destroy() {
    cleanupExecutor.shutdownNow();
    logger.info("CarCache destroyed");
  }

  private static class CacheEntry {
    final List<Car> data;
    final long expiryTime;
    final long createdTime;

    /**
     * Конструктор для создания записи кэша.
     *
     * @param data данные, которые должны храниться в кэше.
     * @param ttl время жизни записи в кэше.
     */
    CacheEntry(List<Car> data, long ttl) {
      this.data = List.copyOf(data);
      this.expiryTime = System.currentTimeMillis() + ttl;
      this.createdTime = System.currentTimeMillis();
    }

    /**
     * Проверяет, истекло ли время жизни записи в кэше.
     *
     * @return true, если запись просрочена.
     */
    boolean isExpired() {
      return System.currentTimeMillis() > expiryTime;
    }
  }

  /**
   * Добавляет или обновляет запись в кэше с использованием стандартного TTL.
   *
   * @param key ключ для записи.
   * @param cars список автомобилей.
   */
  public void put(String key, List<Car> cars) {
    put(key, cars, DEFAULT_TTL);
  }

  /**
   * Добавляет или обновляет запись в кэше с заданным TTL.
   *
   * @param key ключ для записи.
   * @param cars список автомобилей.
   * @param ttl время жизни записи в кэше.
   */
  public synchronized void put(String key, List<Car> cars, long ttl) {
    if (currentSize.get() >= MAX_SIZE) {
      evictOldestEntries();
    }

    cache.put(key, new CacheEntry(cars, ttl));
    currentSize.incrementAndGet();
    logger.debug("Cache PUT - key: {}, size: {}", key, cars.size());
  }

  /**
   * Получает данные из кэша по ключу.
   *
   * @param key ключ для записи.
   * @return данные в виде Optional, если запись не найдена или истекла.
   */
  public Optional<List<Car>> get(String key) {
    CacheEntry entry = cache.get(key);
    if (entry == null) {
      logger.debug("Cache MISS - key: {}", key);
      return Optional.empty();
    }

    if (entry.isExpired()) {
      cache.remove(key);
      currentSize.decrementAndGet();
      logger.debug("Cache EXPIRED - key: {}", key);
      return Optional.empty();
    }

    logger.debug("Cache HIT - key: {}", key);
    return Optional.of(entry.data);
  }

  /**
   * Удаляет запись из кэша по ключу.
   *
   * @param key ключ для записи.
   */
  public synchronized void evict(String key) {
    if (cache.remove(key) != null) {
      currentSize.decrementAndGet();
      logger.debug("Cache EVICT - key: {}", key);
    }
  }

  /**
   * Очищает весь кэш.
   */
  public synchronized void clear() {
    long startTime = System.currentTimeMillis();
    int clearedEntries = cache.size();
    cache.clear();
    currentSize.set(0);
    logger.info("Cache CLEARED - removed {} entries in {}ms",
        clearedEntries, System.currentTimeMillis() - startTime);
  }

  /**
   * Выполняет очистку устаревших записей из кэша.
   */
  private synchronized void cleanupExpiredEntries() {
    long startTime = System.currentTimeMillis();
    int initialSize = cache.size();

    cache.entrySet().removeIf(entry -> {
      boolean expired = entry.getValue().isExpired();
      if (expired) {
        currentSize.decrementAndGet();
      }
      return expired;
    });

    logger.info("Cache cleanup: removed {} expired entries in {}ms",
        initialSize - cache.size(), System.currentTimeMillis() - startTime);
  }

  /**
   * Удаляет старейшие записи из кэша.
   */
  private synchronized void evictOldestEntries() {
    long startTime = System.currentTimeMillis();

    cache.entrySet().stream()
        .sorted(Comparator.comparingLong(entry -> entry.getValue().createdTime))
        .limit(100)
        .forEach(entry -> {
          cache.remove(entry.getKey());
          currentSize.decrementAndGet();
        });

    logger.info("Evicted {} oldest entries in {}ms",
        100, System.currentTimeMillis() - startTime);
  }

  /**
   * Возвращает статистику кэша.
   *
   * @return карта с текущими статистическими данными.
   */
  public Map<String, Object> getCacheStats() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("currentSize", currentSize.get());
    stats.put("maxSize", MAX_SIZE);
    stats.put("defaultTtl", DEFAULT_TTL);
    stats.put("lastCleanup", new Date());
    return stats;
  }
}
