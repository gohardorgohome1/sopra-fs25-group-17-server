package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.doThrow;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.*;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;
import java.lang.Thread;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.core.task.SyncTaskExecutor;
import java.util.concurrent.Executor;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration // Test coverage would not be correct without this class due to asyn method in NotificationService.java
@EnableAsync
class TestAsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return new SyncTaskExecutor(); // Executes tasks synchronously
    }
}


@SpringBootTest(classes = {NotificationService.class, TestAsyncConfig.class})
public class NotificationServiceTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Test
    public void createNotification_validInputs_success() {

        User testUser1 = new User();
        testUser1.setId("1");
        testUser1.setUsername("testUsername1");
        User testUser2 = new User();
        testUser2.setId("2");
        testUser2.setUsername("testUsername2");
        User testUser3 = new User();
        testUser3.setId("3");
        testUser3.setUsername("testUsername3");

        List<User> usersList = List.of(testUser1, testUser2, testUser3);
        when(userRepository.findAll()).thenReturn(usersList);

        ArgumentCaptor<List<Notification>> captor = ArgumentCaptor.forClass(List.class);

        notificationService.createNotificationsForAllUsers("exo1", "testUser", "testExoplanet", "1");

        verify(notificationRepository, times(1)).saveAll(captor.capture());
        List<Notification> savedNotifications = captor.getValue();

        assertEquals(3, savedNotifications.size());

        assertNotNull(savedNotifications.get(0).getUserId()); // Sender has already seen the notification
        assertEquals("exo1", savedNotifications.get(0).getExoplanetId());
        assertEquals("testUser", savedNotifications.get(0).getUploaderUsername());
        assertEquals("testExoplanet", savedNotifications.get(0).getPlanetName());
        assertNotNull(savedNotifications.get(0).getCreatedAt());
        assertTrue(savedNotifications.get(0).isSeen()); // Sender has already seen the notification

        assertNotNull(savedNotifications.get(1).getUserId());
        assertEquals("exo1", savedNotifications.get(1).getExoplanetId());
        assertEquals("testUser", savedNotifications.get(1).getUploaderUsername());
        assertEquals("testExoplanet", savedNotifications.get(1).getPlanetName());
        assertNotNull(savedNotifications.get(1).getCreatedAt());
        assertTrue(!savedNotifications.get(1).isSeen());

        assertNotNull(savedNotifications.get(2).getUserId());
        assertEquals("exo1", savedNotifications.get(2).getExoplanetId());
        assertEquals("testUser", savedNotifications.get(2).getUploaderUsername());
        assertEquals("testExoplanet", savedNotifications.get(2).getPlanetName());
        assertNotNull(savedNotifications.get(2).getCreatedAt());
        assertTrue(!savedNotifications.get(2).isSeen());
    }

    @Test
    public void getAndCheckIfSeenNotifications_validInputs_success() {

        User testUser1 = new User();
        testUser1.setId("1");

        Notification note1 = new Notification();
        note1.setUserId("1");
        note1.setExoplanetId("planet1");
        Notification note2 = new Notification();
        note2.setUserId("1");
        note2.setExoplanetId("planet2");
        Notification note3 = new Notification();
        note3.setUserId("1");
        note3.setExoplanetId("planet3");

        List<Notification> savedNotifications = List.of(note1, note2, note3);
        when(notificationRepository.findByUserId("1")).thenReturn(savedNotifications);

        final List<Notification> allNotifications = notificationService.getAllNotificationsForUser("1");

        assertEquals("planet1", allNotifications.get(0).getExoplanetId()); // Just to check if parameters work
        assertEquals("planet2", allNotifications.get(1).getExoplanetId());
        assertEquals("planet3", allNotifications.get(2).getExoplanetId());
        assertTrue(!allNotifications.get(0).isSeen()); // No notification is seen
        assertTrue(!allNotifications.get(1).isSeen());
        assertTrue(!allNotifications.get(2).isSeen());

        notificationService.markSingleNotificationAsSeen("1", "planet1"); // mark one as seen

        when(notificationRepository.saveAll(anyList())).thenReturn(Collections.emptyList());
        when(notificationRepository.findByUserIdAndSeenFalse("1")).thenReturn(List.of(note2, note3));
        List<Notification> unseenNotifications = notificationService.getUnseenNotificationsForUser("1"); // and then get all others

        assertEquals(2, unseenNotifications.size());
        assertEquals("planet2", unseenNotifications.get(0).getExoplanetId());
        assertEquals("planet3", unseenNotifications.get(1).getExoplanetId());

        notificationService.markNotificationsAsSeen("1"); // mark all as seen

        when(notificationRepository.findByUserIdAndSeenFalse("1")).thenReturn(Collections.emptyList());
        List<Notification> unseenNotifications2 = notificationService.getUnseenNotificationsForUser("1"); // and then get all others
        assertEquals(0, unseenNotifications2.size());
    }
}