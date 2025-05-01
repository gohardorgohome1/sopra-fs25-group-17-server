package ch.uzh.ifi.hase.soprafs24;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker to carry the message back to the client on destinations prefixed with "/topic"
        registry.enableSimpleBroker("/topic");
        // Define the application destination prefix
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        // Register the "/gs-guide-websocket" endpoint, enabling SockJS fallback options
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:8000", "http://localhost:3000", "https://sopra-fs25-group-17-client.vercel.app").withSockJS();
    }
}
