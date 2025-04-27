package com.example.sb.schemas;

import com.example.sb.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data transfer object for User.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
  private final Long id;
  private final String nickName;
  private final String email;
  private final String password;
  private final List<CarDTO> cars;

  /**
   * Constructor for UserDTO.
   *
   * @param id       user ID
   * @param nickName user nickname
   * @param email    user email
   * @param password user password
   * @param cars     list of user's cars
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
   * Converts a User entity to UserDTO.
   *
   * @param user User entity
   * @return UserDTO instance
   */
  public static UserDTO fromEntity(User user) {
    List<CarDTO> carDTOs = user.getCars() != null
        ? user.getCars().stream()
        .map(CarDTO::fromEntity)
        .collect(Collectors.toList())
        : null;

    return new UserDTO(
        user.getId(),
        user.getNickname(),
        user.getEmail(),
        user.getPassword(),
        carDTOs
    );
  }

  /**
   * Converts this DTO to a User entity.
   *
   * @return User entity
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
