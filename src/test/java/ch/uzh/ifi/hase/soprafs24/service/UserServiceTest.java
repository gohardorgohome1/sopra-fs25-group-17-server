package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId("abc123"); // Mongo-style String ID
        testUser.setUsername("testUsername");
    }

    @Test
    public void createUser_validInputs_success() {
        when(userRepository.findByUsername("testUsername")).thenReturn(null);
        when(userRepository.save(any())).thenReturn(testUser);

        User createdUser = userService.createUser(testUser);

        verify(userRepository, times(1)).save(any());

        assertEquals("abc123", createdUser.getId());
        assertEquals("testUsername", createdUser.getUsername());
        assertNotNull(createdUser.getToken());
        assertEquals(UserStatus.ONLINE, createdUser.getStatus());
    }

    @Test
    public void createUser_duplicateUsername_throwsException() {
        when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

        assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));

        verify(userRepository, never()).save(any());
    }
}
