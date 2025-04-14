package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Fetch unseen notifications for a user
    @GetMapping
    public ResponseEntity<List<Notification>> getUnseenNotifications(@RequestParam("userId") String userId) {
        List<Notification> notifications = notificationService.getUnseenNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }
}
