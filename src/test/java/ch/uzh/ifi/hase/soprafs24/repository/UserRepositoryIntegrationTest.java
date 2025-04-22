package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/*
@DataMongoTest
@ExtendWith(SpringExtension.class)
public class UserRepositoryIntegrationTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
    public void setUp() {
      assertTrue(userRepository.count() == 0 || isTestDatabase(), "Refusing to delete users in non-test DB"); // To ensure that the real Database is not wiped
      userRepository.deleteAll();
    }

  @Test
  public void findByUsername_UserExists() {
    // given
    User user = new User();
    user.setId("1");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);
    user.setPassword("testPassword");

    userRepository.save(user);

    // when
    User foundUser = userRepository.findByUsername("testUsername");

    // then
    assertNotNull(foundUser);
    assertEquals("testUsername", foundUser.getUsername());
  }

  @Test
  public void findByUsername_notFound() {
      // when
      User foundUser = userRepository.findByUsername("testWrongUsername");

      // then
      assertNull(foundUser);
  }
}*/