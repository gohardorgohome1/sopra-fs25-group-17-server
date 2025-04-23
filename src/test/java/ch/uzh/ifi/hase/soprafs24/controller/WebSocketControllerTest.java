package ch.uzh.ifi.hase.soprafs24.controller;

import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.mockito.Mockito.*;

class WebSocketControllerTest {

    @Test
    void uploadExoplanet_sendsMessageToTopic() {
        SimpMessagingTemplate messagingTemplate = mock(SimpMessagingTemplate.class);
        WebSocketController controller = new WebSocketController(messagingTemplate);

        String testMessage = "New exoplanet discovered!";

        controller.uploadExoplanet(testMessage);

        verify(messagingTemplate, times(1))
            .convertAndSend("/topic/exoplanets", testMessage);
    }
}
