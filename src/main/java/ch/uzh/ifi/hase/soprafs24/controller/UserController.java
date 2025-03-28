package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

  @GetMapping("/users/{id}")
  @ResponseBody
  public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }
  @PostMapping("/login")
  @ResponseBody
  public User login(@RequestBody UserPostDTO loginRequest) {
        // Call the login method from the UserService
        return userService.login(loginRequest.getUsername(), loginRequest.getPassword());
    }

  @PutMapping("/users/{id}/logout")
  @ResponseBody
  public ResponseEntity<Void> logoutUser(@PathVariable Long id) {
      userService.logoutUser(id);
      return ResponseEntity.ok().build();
    }
  
  @PutMapping("/users/{id}")
  @ResponseBody
  public ResponseEntity<UserGetDTO> updateUser(@PathVariable Long id, @RequestBody UserPostDTO updatedUser) {
            // 2. Convert UserPostDTO to User entity --> birthday becomes LocalDate in User.java
      User user = convertToUser(updatedUser);

      // 3. Call the service layer to update the user --> UserService.java operates with birthday as a LocalDate
      user = userService.updateUser(id, user);

      // 4. Convert updated User entity to UserGetDTO --> convert birthday back to string
      UserGetDTO userGetDTO = convertToUserGetDTO(user);

      // 5. Return the updated user as a response --> send birthday as a string back to frontend
      //return ResponseEntity.ok(userGetDTO);
      return ResponseEntity.noContent().build();
  }

  private User convertToUser(UserPostDTO dto) {
      User user = new User();
      user.setUsername(dto.getUsername());

      return user;
  }

  private UserGetDTO convertToUserGetDTO(User user) {
      UserGetDTO userGetDTO = new UserGetDTO();
      userGetDTO.setUsername(user.getUsername());

      return userGetDTO;
  }

    

}
