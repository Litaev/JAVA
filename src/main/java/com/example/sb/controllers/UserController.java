package com.example.sb.controllers;

import com.example.sb.schemas.UserDTO;
import com.example.sb.service.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user-related API requests.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

  private final UserService userService;

  /**
   * Constructs a {@code UserController} with the specified user service.
   *
   * @param userService the service to handle user operations
   */
  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Creates a new user.
   *
   * @param userDto the user data
   * @return the created user
   */
  @PostMapping
  public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
    UserDTO createdUser = userService.createUser(userDto);
    return ResponseEntity.ok(createdUser);
  }

  /**
   * Retrieves all users.
   *
   * @return list of all users
   */
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    return ResponseEntity.ok(users);
  }

  /**
   * Retrieves a user by ID.
   *
   * @param userId the ID of the user
   * @return the user if found, otherwise 404
   */
  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
    Optional<UserDTO> user = userService.getUserById(userId);
    return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Updates an existing user.
   *
   * @param userId the ID of the user
   * @param userDto the new user data
   * @return the updated user if found, otherwise 404
   */
  @PutMapping("/{userId}")
  public ResponseEntity<UserDTO> updateUser(
      @PathVariable Long userId, @RequestBody UserDTO userDto) {
    Optional<UserDTO> updatedUser = userService.updateUser(userId, userDto);
    return updatedUser.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * Deletes a user by ID.
   *
   * @param userId the ID of the user
   * @return 200 OK if deleted, otherwise 404
   */
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
    boolean isDeleted = userService.deleteUser(userId);
    if (isDeleted) {
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
