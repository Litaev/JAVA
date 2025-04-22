package com.example.sb.service;

import com.example.sb.models.User;
import com.example.sb.repository.UserRepository;
import com.example.sb.schemas.UserDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for handling user-related business logic.
 */
@Service
public class UserService {

  private final UserRepository userRepository;

  /**
   * Constructs a {@code UserService} instance.
   *
   * @param userRepository the repository for {@link User} entities
   */
  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  /**
   * Creates a new user.
   *
   * @param userDto the DTO containing user details
   * @return the created user DTO
   */
  public UserDTO createUser(UserDTO userDto) {
    User user = userDto.toEntity();
    return UserDTO.fromEntity(userRepository.save(user));
  }

  /**
   * Retrieves all users.
   *
   * @return a list of all user DTOs
   */
  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(UserDTO::fromEntity)
        .toList();
  }

  /**
   * Retrieves a user by its ID.
   *
   * @param id the ID of the user to retrieve
   * @return an optional user DTO
   */
  public Optional<UserDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserDTO::fromEntity);
  }

  /**
   * Updates an existing user with the provided details.
   *
   * @param id the ID of the user to update
   * @param userDto the DTO containing updated user details
   * @return an optional updated user DTO
   */
  public Optional<UserDTO> updateUser(Long id, UserDTO userDto) {
    if (userRepository.existsById(id)) {
      User updatedUser = userDto.toEntity();
      updatedUser.setId(id);
      return Optional.of(UserDTO.fromEntity(userRepository.save(updatedUser)));
    }
    return Optional.empty();
  }

  /**
   * Deletes a user by its ID.
   *
   * @param id the ID of the user to delete
   * @return true if the user was deleted, false otherwise
   */
  public boolean deleteUser(Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }
}
