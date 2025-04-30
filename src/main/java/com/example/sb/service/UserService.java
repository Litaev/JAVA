package com.example.sb.service;

import com.example.sb.exceptions.ValidationException;
import com.example.sb.models.User;
import com.example.sb.repository.UserRepository;
import com.example.sb.schemas.UserDTO;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;


  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Transactional
  public UserDTO createUser(UserDTO userDto) {
    if (userRepository.existsByEmail(userDto.getEmail())) {
      throw new ValidationException("Email already exists");
    }
    if (userRepository.existsByNickname(userDto.getNickName())) {
      throw new ValidationException("Nickname already exists");
    }

    User user = userDto.toEntity();
    return UserDTO.fromEntity(userRepository.save(user));
  }

  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream()
        .map(UserDTO::fromEntity)
        .toList();
  }


  public Optional<UserDTO> getUserById(Long id) {
    return userRepository.findById(id)
        .map(UserDTO::fromEntity);
  }


  public Optional<UserDTO> updateUser(Long id, UserDTO userDto) {
    if (userRepository.existsById(id)) {
      User updatedUser = userDto.toEntity();
      updatedUser.setId(id);
      return Optional.of(UserDTO.fromEntity(userRepository.save(updatedUser)));
    }
    return Optional.empty();
  }


  public boolean deleteUser(Long id) {
    if (userRepository.existsById(id)) {
      userRepository.deleteById(id);
      return true;
    }
    return false;
  }


  public List<UserDTO> getUsersWithCarFilters(
      String nickName,
      Integer minYear,
      Integer maxYear,
      Integer minMileage,
      Integer maxMileage,
      String fuelType,
      String carName) {
    List<User> users = userRepository.findUsersWithCarFilters(
        nickName, minYear, maxYear, minMileage, maxMileage, fuelType, carName);

    return convertToDtoList(users);
  }

  private List<UserDTO> convertToDtoList(List<User> users) {
    return users.stream()
        .map(UserDTO::fromEntity)
        .toList();
  }
}
