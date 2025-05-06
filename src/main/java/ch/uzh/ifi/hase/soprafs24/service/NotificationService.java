package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.NotificationRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Async
    public void createNotificationsForAllUsers(String exoplanetId, String uploaderUsername, String planetName, String ownerId) {
        // Fetch all users to notify
        List<User> allUsers = userRepository.findAll();

        List<Notification> notifications = new ArrayList<>();
        for (User u : allUsers) {
            Notification n = new Notification();
            n.setUserId(u.getId());
            n.setExoplanetId(exoplanetId);
            n.setUploaderUsername(uploaderUsername);
            n.setPlanetName(planetName);
            n.setSeen(u.getId().equals(ownerId)); // uploader gets "seen = true"
            // n.setSeen(false); // New notifications are unseen
            n.setCreatedAt(LocalDateTime.now());
            notifications.add(n);
        }

        // Save all notifications
        notificationRepository.saveAll(notifications);
    }

    public List<Notification> getAllNotificationsForUser(String userId) {
        return notificationRepository.findByUserId(userId);
    }
    
    // Fetch unseen notifications for a user
    public List<Notification> getUnseenNotificationsForUser(String userId) {
        List<Notification> notifs = notificationRepository.findByUserIdAndSeenFalse(userId);

        for (Notification n : notifs) {
            n.setSeen(true); // Mark as seen
        }
        
        notificationRepository.saveAll(notifs); // Persist changes

        return notifs;
    }

    public void markNotificationsAsSeen(String userId){
        List<Notification> notifs = notificationRepository.findByUserIdAndSeenFalse(userId);
        for (Notification n : notifs) {
            n.setSeen(true); // Mark as seen
        }
        notificationRepository.saveAll(notifs); // Persist changes

    }  

    public void markSingleNotificationAsSeen(String userId, String exoplanetId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndExoplanetId(userId, exoplanetId);
    
        for (Notification n : notifications) {
            if (!n.isSeen()) {
                n.setSeen(true);
            }
        }

        notificationRepository.saveAll(notifications);
    }
    

}
