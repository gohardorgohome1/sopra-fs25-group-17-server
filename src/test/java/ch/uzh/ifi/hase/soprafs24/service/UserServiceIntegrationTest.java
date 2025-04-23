package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@Import(UserService.class)
@ActiveProfiles("test")
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void createUser_validInputs_success() {
        User user = new User();
        user.setUsername("testuser");

        User createdUser = userService.createUser(user);

        assertNotNull(createdUser.getId());
        assertEquals("testuser", createdUser.getUsername());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
        assertNotNull(createdUser.getToken());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        User user1 = new User();
        user1.setUsername("dupeUser");

        userService.createUser(user1);

        User user2 = new User();
        user2.setUsername("dupeUser");

        assertThrows(ResponseStatusException.class, () -> userService.createUser(user2));
    }
}
