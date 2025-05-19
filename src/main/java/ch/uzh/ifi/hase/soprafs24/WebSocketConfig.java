package ch.uzh.ifi.hase.soprafs24;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.Message;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker to carry the message back to the client on destinations prefixed with "/topic"
        registry.enableSimpleBroker("/topic", "/queue");
        // Define the application destination prefix
        registry.setApplicationDestinationPrefixes("/app");
        // Enable /user/ prefix for private messaging
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(org.springframework.web.socket.config.annotation.StompEndpointRegistry registry) {
        // Register the "/gs-guide-websocket" endpoint, enabling SockJS fallback options
        registry.addEndpoint("/ws").setAllowedOrigins("http://localhost:8000", "http://localhost:3000", "https://sopra-fs25-group-17-client.vercel.app").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String userId = accessor.getFirstNativeHeader("userId");
                    if (userId != null) {
                        accessor.setUser(() -> userId); // use a simple Principal
                    }
                }
                return message;
            }
        });
    }
}
