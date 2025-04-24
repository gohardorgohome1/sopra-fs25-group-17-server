package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController {

  private final UserService userService;
  private final DTOMapper dtoMapper;

  public UserController(UserService userService, DTOMapper dtoMapper) {
      this.userService = userService;
      this.dtoMapper = dtoMapper;
  }

  @GetMapping("/users")
  public List<UserGetDTO> getAllUsers() {
    return userService.getUsers().stream()
      .map(DTOMapper.INSTANCE::convertEntityToUserGetDTO)
      .collect(Collectors.toList());
  }

  @GetMapping("/users/{id}")
  public ResponseEntity<UserGetDTO> getUserById(@PathVariable String id) {
    User user = userService.getUserById(id);
    return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
  }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
    User createdUser = userService.createUser(userInput);
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PostMapping("/login")
  @ResponseBody
  public User login(@RequestBody UserPostDTO loginRequest) {
        // Call the login method from the UserService
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

  @PutMapping("/users/{id}/logout")
  public ResponseEntity<Void> logoutUser(@PathVariable String id) {
    userService.logoutUser(id);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/users/{id}")
  public ResponseEntity<Void> updateUser(@PathVariable String id, @RequestBody UserPostDTO updatedUser) {
    User user = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(updatedUser);
    userService.updateUser(id, user);
    return ResponseEntity.noContent().build();
  }
}