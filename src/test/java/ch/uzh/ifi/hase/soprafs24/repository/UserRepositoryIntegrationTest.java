package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void findByUsername_success() {
        User user = new User();
        user.setUsername("mongoTestUser");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now());

        userRepository.save(user);

        User found = userRepository.findByUsername("mongoTestUser");

        assertNotNull(found);
        assertEquals("mongoTestUser", found.getUsername());
    }

    @Test
    void saveUser_success() {
        User user = new User();
        user.setUsername("saveTest");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.now());

        User saved = userRepository.save(user);

        assertNotNull(saved.getId());
        assertEquals("saveTest", saved.getUsername());
    }
}
