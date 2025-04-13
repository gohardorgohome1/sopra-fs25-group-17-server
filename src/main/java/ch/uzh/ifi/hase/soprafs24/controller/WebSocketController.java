package ch.uzh.ifi.hase.soprafs24.controller;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // This method sends messages to clients who are subscribed to /topic/exoplanets
    @MessageMapping("/uploadExoplanet")
    public void uploadExoplanet(String message) {
        messagingTemplate.convertAndSend("/topic/exoplanets", message);
    }
}