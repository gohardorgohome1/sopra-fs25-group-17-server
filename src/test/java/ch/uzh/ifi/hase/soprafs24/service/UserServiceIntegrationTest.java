package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
/*
@DataMongoTest
@ActiveProfiles("test")
@Import(UserService.class)
public class UserServiceIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    //assertTrue(userRepository.count() == 0 || isTestDatabase(), "Refusing to delete users in non-test DB"); // To ensure that the real Database is not wiped
    userRepository.deleteAll();
  }
  // Create a User with valid inputs
  @Test
  public void createUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setUsername("testUsername");

    // when
    User createdUser = userService.createUser(testUser);

    // then
    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }
  // Create a User with an already existing Username
  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setUsername("testUsername");
    User createdUser = userService.createUser(testUser);

    userRepository.save(createdUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // Set the exact same Username
    testUser2.setUsername("testUsername");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
  // Login with valid inputs
  @Test
  public void loginUser_validInputs_success() {
    // given
    assertNull(userRepository.findByUsername("testUsername"));

    User testUser = new User();
    testUser.setUsername("testUsername");
    testUser.setPassword("testPassword");

    User createdUser = userService.createUser(testUser);
    createdUser.setStatus(UserStatus.OFFLINE);
    assertEquals(UserStatus.OFFLINE, createdUser.getStatus());

    // when
    User loggedInUser = userService.login("testUsername", "testPassword");

    // then
    assertEquals(testUser.getId(), loggedInUser.getId());
    assertEquals(testUser.getUsername(), loggedInUser.getUsername());
    assertNotNull(loggedInUser.getToken());
    assertEquals(UserStatus.ONLINE, loggedInUser.getStatus());
  }
}*/