package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Notification;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.NudgeRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatInviteDTO;
import ch.uzh.ifi.hase.soprafs24.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Fetch unseen notifications for a user --> this marks them as seen.
    @GetMapping
    public ResponseEntity<List<Notification>> getUnseenNotifications(@RequestParam("userId") String userId) {
        List<Notification> notifications = notificationService.getUnseenNotificationsForUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/all")
    public List<Notification> getAllNotifications(@RequestParam String userId) {
        return notificationService.getAllNotificationsForUser(userId);
    }

    // this marks ALL notifications as seen, this is for the notifications the user missed when offline.
    @PutMapping("/mark-seen")
    public ResponseEntity<Void> markNotificationsAsSeen(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        notificationService.markNotificationsAsSeen(userId);
        return ResponseEntity.ok().build();
}
    // this marks SINGLE notifications as seen, this is used for the notifications the user sees in real time.
    @PutMapping("/mark-seen-single")
    public ResponseEntity<Void> markSingleNotificationAsSeen(@RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String exoplanetId = body.get("exoplanetId");

        notificationService.markSingleNotificationAsSeen(userId, exoplanetId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/nudge")
    @ResponseStatus(HttpStatus.OK)
    public void sendNudge(@RequestBody NudgeRequestDTO dto) {
        User fromUser = userService.getUserById(dto.getFromUserId());
        User toUser = userService.getUserById(dto.getToUserId());

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "nudge");
        payload.put("fromUsername", fromUser.getUsername());
        payload.put("fromUserId", fromUser.getId());

        // Each user subscribes to their own topic: /user/{userId}/queue/notifications
        messagingTemplate.convertAndSend("/user/" + toUser.getId() + "/queue/notifications", payload);
    }

    @PostMapping("/chat-invite")
    @ResponseStatus(HttpStatus.OK)
    public void sendChatInviteNotification(@RequestBody ChatInviteDTO dto) {
        User fromUser = userService.getUserById(dto.getFromUserId());

        for (String toUserId : dto.getToUserIds()) {
            User toUser = userService.getUserById(toUserId);

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "chat-invite");
            payload.put("fromUsername", fromUser.getUsername());
            payload.put("fromUserId", fromUser.getId());

            // Each user subscribes to their own topic
            messagingTemplate.convertAndSend("/user/" + toUser.getId() + "/queue/notifications", payload);
        }
    }


}
