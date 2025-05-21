package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

  private final Logger log = LoggerFactory.getLogger(UserService.class);
  private final UserRepository userRepository;

  @Autowired
  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<User> getUsers() {
    return this.userRepository.findAll();
  }

  public User createUser(User newUser) {
    newUser.setToken(UUID.randomUUID().toString());
    newUser.setStatus(UserStatus.ONLINE);
    newUser.setCreationDate(LocalDateTime.now());
    checkIfUserExists(newUser);
    return userRepository.save(newUser);
  }

  public User login(String username, String password) {
    User user = userRepository.findByUsername(username);
    if (user == null || !password.equals(user.getPassword())) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
    }
    user.setStatus(UserStatus.ONLINE);
    userRepository.save(user);
    return user;
  }

  public void logoutUser(String id) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    user.setStatus(UserStatus.OFFLINE);
    userRepository.save(user);
  }

  public User updateUser(String id, User updatedUser) {
    User user = userRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    if (updatedUser.getUsername() != null && !updatedUser.getUsername().trim().isEmpty()) {
      user.setUsername(updatedUser.getUsername());
    }
    return userRepository.save(user);
  }

  public User getUserById(String id) {
    return userRepository.findById(id)
      .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
  }

  private void checkIfUserExists(User userToBeCreated) {
    if (userRepository.findByUsername(userToBeCreated.getUsername()) != null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The username '%s' is not unique.", userToBeCreated.getUsername()));
    }
  }

  public void setUsername(String id, String username) {
      User user = userRepository.findById(id)
              .orElseThrow(() ->
                      new RuntimeException("User not found"));
      user.setUsername(username);
      checkIfUserExists(user); // avoid duplicate names also after changing a username
      userRepository.save(user);
  }
}
