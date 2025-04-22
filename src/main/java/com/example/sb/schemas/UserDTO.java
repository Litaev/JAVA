package com.example.sb.schemas;

import com.example.sb.models.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Data Transfer Object for the {@link User} entity.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

  private final Long id;
  private final String nickName;
  private final String email;
  private final String password;

  /**
   * Constructs a {@code UserDTO} instance with all fields.
   *
   * @param id the user ID
   * @param nickName the user's nickname
   * @param email the user's email
   * @param password the user's password
   */
  public UserDTO(Long id, String nickName, String email, String password) {
    this.id = id;
    this.nickName = nickName;
    this.email = email;
    this.password = password;
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

  /**
   * Converts a {@link User} entity to a {@code UserDTO}.
   *
   * @param user the user entity
   * @return a corresponding DTO
   */
  public static UserDTO fromEntity(User user) {
    return new UserDTO(
        user.getId(),
        user.getNickname(),
        user.getEmail(),
        user.getPassword());
  }

  /**
   * Converts this DTO back to a {@link User} entity.
   *
   * @return the user entity
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
