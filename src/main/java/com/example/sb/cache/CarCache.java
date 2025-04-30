package com.example.sb.cache;

import com.example.sb.models.Car;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Comparator;
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
 * Класс CarCache реализует кэш для хранения данных об автомобилях.
 * Кэш использует ConcurrentHashMap для хранения записей, поддерживает ограничение по размеру
 * и время жизни записей. Периодически выполняется очистка устаревших записей.
 */
@Component
public class CarCache {


  private static final Logger log = LoggerFactory.getLogger(CarCache.class);

  private final Map<String, CacheEntry> storage = new ConcurrentHashMap<>();
  private final AtomicInteger size = new AtomicInteger(0);
  private static final int LIMIT = 100;
  private static final int LIMIT_AFTER_OVERFLOW = 50;
  private static final long TTL_DEFAULT = 300000;
  private final ScheduledExecutorService cleaner;

  /**
   * Конструктор класса CarCache, инициализирует ScheduledExecutorService.
   */
  public CarCache() {
    cleaner = Executors.newSingleThreadScheduledExecutor();
  }

  /**
   * Метод инициализации, который запускает периодическую очистку кэша.
   */
  @PostConstruct
  public void setUp() {
    cleaner.scheduleAtFixedRate(this::purgeExpired, 300, 300, TimeUnit.SECONDS);
    log.info("CarCache initialized with cleanup task every 300 seconds");
  }

  /**
   * Метод для уничтожения ресурсов перед завершением работы.
   */
  @PreDestroy
  public void tearDown() {
    cleaner.shutdownNow();
    log.info("CarCache resources cleaned up");
  }

  /**
   * Внутренний класс, представляющий запись в кэше.
   */
  private static class CacheEntry {
    private final List<Car> cars;
    private final long expiresAt;
    private final long createdAt;

    /**
     * Конструктор для создания записи кэша.
     *
     * @param cars список автомобилей для хранения.
     * @param ttl время жизни записи в миллисекундах.
     */
    CacheEntry(List<Car> cars, long ttl) {
      this.cars = List.copyOf(cars);
      long now = System.currentTimeMillis();
      this.expiresAt = now + ttl;
      this.createdAt = now;
    }

    /**
     * Проверяет, истекло ли время жизни записи.
     *
     * @return true, если запись просрочена.
     */
    boolean hasExpired() {
      return System.currentTimeMillis() > expiresAt;
    }
  }

  /**
   * Добавляет или обновляет запись в кэше с использованием стандартного TTL.
   *
   * @param key ключ записи.
   * @param cars список автомобилей.
   */
  public void put(String key, List<Car> cars) {
    put(key, cars, TTL_DEFAULT);
  }

  /**
   * Добавляет или обновляет запись в кэше с заданным TTL.
   *
   * @param key ключ записи.
   * @param cars список автомобилей.
   * @param ttl время жизни записи.
   */
  public synchronized void put(String key, List<Car> cars, long ttl) {
    if (size.get() >= LIMIT) {
      removeOldest();
    }
    storage.put(key, new CacheEntry(cars, ttl));
    size.incrementAndGet();
  }

  /**
   * Получает данные из кэша по ключу.
   *
   * @param key ключ записи.
   * @return данные в виде Optional, если запись найдена и не истекла.
   */
  public Optional<List<Car>> get(String key) {
    CacheEntry entry = storage.get(key);
    if (entry == null) {
      return Optional.empty();
    }
    if (entry.hasExpired()) {
      storage.remove(key);
      size.decrementAndGet();
      return Optional.empty();
    }
    return Optional.of(entry.cars);
  }

  /**
   * Удаляет запись из кэша по ключу.
   *
   * @param key ключ записи.
   */
  public synchronized void evict(String key) {
    if (storage.remove(key) != null) {
      size.decrementAndGet();
    }
  }

  /**
   * Полностью очищает кэш.
   */
  public synchronized void clear() {
    int removed = storage.size();
    storage.clear();
    size.set(0);
    log.info("CarCache cleared: {} entries removed", removed);
  }

  /**
   * Выполняет очистку просроченных записей из кэша.
   */
  private synchronized void purgeExpired() {
    int before = storage.size();
    storage.entrySet().removeIf(entry -> {
      if (entry.getValue().hasExpired()) {
        size.decrementAndGet();
        return true;
      }
      return false;
    });
    int after = storage.size();
    log.info("CarCache cleanup: {} expired entries removed", (before - after));
  }

  /**
   * Удаляет старейшие записи из кэша, если превышен лимит.
   */
  private synchronized void removeOldest() {
    storage.entrySet().stream()
        .sorted(Comparator.comparingLong(e -> e.getValue().createdAt))
        .limit(LIMIT_AFTER_OVERFLOW)
        .forEach(entry -> {
          storage.remove(entry.getKey());
          size.decrementAndGet();
        });
    log.info("CarCache evicted {} oldest entries to maintain size limit", LIMIT_AFTER_OVERFLOW);
  }

}
