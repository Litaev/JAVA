package com.example.sb.schemas;

import com.example.sb.models.User;
import com.example.sb.validation.UniqueEmail;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object (DTO) for {@link User} entity.
 *
 * <p>Contains validation constraints for user data and conversion methods between DTO and entity.
 * Excludes null values during JSON serialization.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

  @Null(message = "ID should not be provided for creation")
  private final Long id;

  @NotBlank(message = "Nickname is required")
  @Size(min = 3, max = 20, message = "Nickname must be between 3 and 20 characters")
  private final String nickName;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @UniqueEmail
  private final String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*$",
      message = "Password must contain at least one digit, one lowercase and one uppercase letter"
  )
  private final String password;

  private final List<CarDTO> cars;

  /**
   * Constructs a new UserDTO with specified parameters.
   *
   * @param id the user ID (should be null for creation)
   * @param nickName the user nickname (3-20 characters)
   * @param email the user email (must be unique and valid)
   * @param password the user password (min 8 chars with mixed case and digit)
   * @param cars list of associated cars (optional)
   */
  public UserDTO(
      Long id,
      String nickName,
      String email,
      String password,
      List<CarDTO> cars) {
    this.id = id;
    this.nickName = nickName;
    this.email = email;
    this.password = password;
    this.cars = cars;
  }

  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  @JsonProperty("nickName")
  public String getNickName() {
    return nickName;
  }

  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  @JsonProperty("cars")
  public List<CarDTO> getCars() {
    return cars;
  }

  /**
   * Converts User entity to UserDTO.
   *
   * @param user the entity to convert
   * @return new UserDTO instance with converted cars if present
   */
  public static UserDTO fromEntity(User user) {
    if (user == null) {
      return null;
    }

    List<CarDTO> carDTOs = user.getCars() != null
        ? user.getCars().stream()
        .map(CarDTO::fromEntity)
        .collect(Collectors.toList())
        : null;

    return new UserDTO(
        user.getId(),
        user.getNickname(),
        user.getEmail(),
        null, // Не возвращаем пароль
        carDTOs
    );
  }

  /**
   * Converts this DTO to User entity.
   *
   * <p>Note: Does not include cars in the conversion. Cars should be handled separately.
   *
   * @return new User entity built from this DTO
   */
  public User toEntity() {
    return User.builder()
        .id(this.id)
        .nickname(this.nickName)
        .email(this.email)
        .password(this.password)
        .build();
  }
}