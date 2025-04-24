package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doThrow;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setId("1");
    testUser.setUsername("testUsername");

    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }
  // Ceate a User with valid input
  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being saved in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

        verify(userRepository, times(1)).save(any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }
  // Create a User with already existing Username
  @Test
  public void createUser_duplicateName_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
    // Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }
  // get all Users
  @Test
  public void getUsers_success() {
    userService.createUser(testUser);

    Mockito.when(userRepository.findAll()).thenReturn(List.of(testUser));

    List<User> allUsers = userService.getUsers();

    assertEquals(allUsers.size(), 1);

    User savedUser = allUsers.get(0);
    assertEquals(testUser.getUsername(), savedUser.getUsername());
    assertEquals(testUser.getId(), savedUser.getId());
  }
  // getUserById with valid input
  @Test
  public void getUserById_validInput_success() {
    userService.createUser(testUser);

    Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

    User savedUser = userService.getUserById("1");
    assertEquals(testUser.getUsername(), savedUser.getUsername());
    assertEquals(testUser.getId(), savedUser.getId());
    assertNotNull(savedUser.getToken());
    assertEquals(UserStatus.ONLINE, savedUser.getStatus());
  }
  // getUserById with invalid ID as input
  @Test
  public void getUserById_invalidInput_not_found() {
    userService.createUser(testUser);

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
      .when(userRepository).findById("99");

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.getUserById("99");
    });
    
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
  }
  // Login with valid input
  @Test
  public void loginUser_validInput_success() {
    testUser.setPassword("testPassword");
    userService.createUser(testUser);
    testUser.setStatus(UserStatus.OFFLINE);

    assertEquals(UserStatus.OFFLINE, testUser.getStatus()); // Should be OFFLINE before login (for testing)

    Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

    User loggedInUser = userService.login("testUsername", "testPassword");
    assertEquals(testUser.getUsername(), loggedInUser.getUsername());
    assertEquals(testUser.getPassword(), loggedInUser.getPassword());
    assertEquals(testUser.getId(), loggedInUser.getId());
    assertNotNull(loggedInUser.getToken());
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus()); // Must be ONLINE now
  }
  // Login with wrong Password
  @Test
  public void loginUser_invalidInput_wrong_password() {
    testUser.setPassword("testPassword");
    userService.createUser(testUser);

    Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.login("testUsername", "testWrongPassword");
    });
    
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid username or password.", exception.getReason());
  }
  // Login with inexistant User
  @Test
  public void loginUser_invalidInput_inexistant_user() {
    testUser.setPassword("testPassword");
    userService.createUser(testUser);

    Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.login("testWrongUsername", "testPassword");
    });
    
    assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    assertEquals("Invalid username or password.", exception.getReason());
  }
  // Logout with valid UserId
  @Test
  public void logoutUser_validInput_success() {
    userService.createUser(testUser);

    Mockito.when(userRepository.findById("1")).thenReturn(Optional.of(testUser));

    userService.logoutUser("1");
    User loggedOutUser = userRepository.findById("1").get(); // logoutUser() returns void, therefore this extra step is needed
    assertEquals(testUser.getUsername(), loggedOutUser.getUsername());
    assertEquals(testUser.getId(), loggedOutUser.getId());
    assertNotNull(loggedOutUser.getToken());
    assertEquals(UserStatus.OFFLINE, loggedOutUser.getStatus()); // Must be OFFLINE
  }
  //Logout with invalid UserId
  @Test
  public void logoutUser_invalidInput_inexistant_user() {
    userService.createUser(testUser);

    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"))
      .when(userRepository).findById("99");

    ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
      userService.logoutUser("99");
    });
    
    assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    assertEquals("User not found", exception.getReason());
  }
}