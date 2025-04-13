package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatResponseDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@RestController
public class OpenAIController {

    private static final String OPENAI_API_KEY = "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"; // Pon aquí tu clave secreta
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @PostMapping("/openai/chat")
    public ResponseEntity<ChatResponseDTO> chatWithOpenAI(@RequestBody ChatRequestDTO chatRequest) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini-2024-07-18");
        requestBody.put("messages", chatRequest.getMessages());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(OPENAI_API_KEY);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, request, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String reply = (String) message.get("content");

            return ResponseEntity.ok(new ChatResponseDTO(reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(new ChatResponseDTO("Error calling OpenAI: " + e.getMessage()));
        }
    }
}
