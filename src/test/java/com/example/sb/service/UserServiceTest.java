package com.example.sb.service;

import com.example.sb.exceptions.ValidationException;
import com.example.sb.models.User;
import com.example.sb.repository.UserRepository;
import com.example.sb.schemas.UserDTO;
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
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private UserDTO testUserDto;
  private User testUser;

  @BeforeEach
  void setUp() {
    testUserDto = new UserDTO(null, "testUser", "test@example.com", "Password1", null);
    testUser = testUserDto.toEntity();
    testUser.setId(1L);
  }

  @Test
  void createUser_ShouldReturnCreatedUser() {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByNickname(anyString())).thenReturn(false);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    UserDTO result = userService.createUser(testUserDto);

    assertNotNull(result);
    assertEquals(testUserDto.getNickName(), result.getNickName());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void createUser_ShouldThrowWhenEmailExists() {
    when(userRepository.existsByEmail(anyString())).thenReturn(true);

    assertThrows(ValidationException.class, () -> userService.createUser(testUserDto));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void createUser_ShouldThrowWhenNicknameExists() {
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(userRepository.existsByNickname(anyString())).thenReturn(true);

    assertThrows(ValidationException.class, () -> userService.createUser(testUserDto));
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void getAllUsers_ShouldReturnEmptyList() {
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    List<UserDTO> result = userService.getAllUsers();

    assertTrue(result.isEmpty());
  }

  @Test
  void getAllUsers_ShouldReturnUsersList() {
    when(userRepository.findAll()).thenReturn(List.of(testUser));

    List<UserDTO> result = userService.getAllUsers();

    assertEquals(1, result.size());
    assertEquals(testUser.getNickname(), result.get(0).getNickName());
  }

  @Test
  void getUserById_ShouldReturnUser() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));

    Optional<UserDTO> result = userService.getUserById(1L);

    assertTrue(result.isPresent());
    assertEquals(testUser.getNickname(), result.get().getNickName());
  }

  @Test
  void getUserById_ShouldReturnEmpty() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    Optional<UserDTO> result = userService.getUserById(1L);

    assertTrue(result.isEmpty());
  }

  @Test
  void updateUser_ShouldUpdateExistingUser() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    Optional<UserDTO> result = userService.updateUser(1L, testUserDto);

    assertTrue(result.isPresent());
    assertEquals(testUserDto.getNickName(), result.get().getNickName());
  }

  @Test
  void updateUser_ShouldReturnEmptyForNonExistingUser() {
    when(userRepository.existsById(anyLong())).thenReturn(false);

    Optional<UserDTO> result = userService.updateUser(1L, testUserDto);

    assertTrue(result.isEmpty());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void deleteUser_ShouldReturnTrueForExistingUser() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    doNothing().when(userRepository).deleteById(anyLong());

    boolean result = userService.deleteUser(1L);

    assertTrue(result);
    verify(userRepository).deleteById(1L);
  }

  @Test
  void deleteUser_ShouldReturnFalseForNonExistingUser() {
    when(userRepository.existsById(anyLong())).thenReturn(false);

    boolean result = userService.deleteUser(1L);

    assertFalse(result);
    verify(userRepository, never()).deleteById(anyLong());
  }

  @Test
  void getUsersWithCarFilters_ShouldReturnFilteredUsers() {
    when(userRepository.findUsersWithCarFilters(
        anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(List.of(testUser));

    List<UserDTO> result = userService.getUsersWithCarFilters(
        "test", 2000, 2020, 0, 100000, "petrol", "Toyota");

    assertEquals(1, result.size());
    assertEquals(testUser.getNickname(), result.get(0).getNickName());
  }

  @Test
  void getUsersWithCarFilters_ShouldReturnEmptyList() {
    when(userRepository.findUsersWithCarFilters(
        anyString(), any(), any(), any(), any(), any(), any()))
        .thenReturn(Collections.emptyList());

    List<UserDTO> result = userService.getUsersWithCarFilters(
        "test", 2000, 2020, 0, 100000, "petrol", "Toyota");

    assertTrue(result.isEmpty());
  }



  @Test
  void updateUser_ShouldPreserveId() {
    when(userRepository.existsById(anyLong())).thenReturn(true);
    when(userRepository.save(any(User.class))).thenReturn(testUser);

    UserDTO updateDto = new UserDTO(2L, "updatedUser", "updated@example.com", "NewPassword1", null);
    Optional<UserDTO> result = userService.updateUser(1L, updateDto);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId()); // ID должно остаться из пути, а не из DTO
  }

  @Test
  void getUsersWithCarFilters_ShouldHandleNullFilters() {
    when(userRepository.findUsersWithCarFilters(
        any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(List.of(testUser));

    List<UserDTO> result = userService.getUsersWithCarFilters(
        null, null, null, null, null, null, null);

    assertEquals(1, result.size());
  }

  @Test
  void fromEntity_ShouldConvertCorrectly() {
    User user = new User();
    user.setId(1L);
    user.setNickname("testUser");
    user.setEmail("test@example.com");
    user.setPassword("password");
    user.setCars(Collections.emptyList()); // Инициализируем cars

    UserDTO result = UserDTO.fromEntity(user);

    assertEquals(user.getId(), result.getId());
    assertEquals(user.getNickname(), result.getNickName());
    assertEquals(user.getEmail(), result.getEmail());
    assertNull(result.getPassword());
    assertNotNull(result.getCars());
  }

  @Test
  void toEntity_ShouldConvertCorrectly() {
    UserDTO dto = new UserDTO(null, "testUser", "test@example.com", "Password1", null);

    User result = dto.toEntity();

    assertEquals(dto.getNickName(), result.getNickname());
    assertEquals(dto.getEmail(), result.getEmail());
    assertEquals(dto.getPassword(), result.getPassword()); // Пароль должен сохраняться
    assertNull(result.getId());
    assertNull(result.getCars());
  }

  @Test
  void fromEntity_ShouldHandleNullInput() {
    assertNull(UserDTO.fromEntity(null));
  }
}