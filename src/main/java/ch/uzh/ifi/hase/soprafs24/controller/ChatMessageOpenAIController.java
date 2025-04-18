package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessageOpenAI;
import ch.uzh.ifi.hase.soprafs24.repository.ChatMessageOpenAIRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatRequestDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatResponseDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/openai")
public class ChatMessageOpenAIController {

    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Autowired
    private ChatMessageOpenAIRepository chatRepo;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDTO> chatWithOpenAI(@RequestBody ChatRequestDTO chatRequest) {
        RestTemplate restTemplate = new RestTemplate();


        for (ChatRequestDTO.Message msg : chatRequest.getMessages()) {
            ChatMessageOpenAI userMsg = new ChatMessageOpenAI();
            userMsg.setUserId(chatRequest.getUserId());
            userMsg.setSenderName(chatRequest.getUsername()); 
            userMsg.setRole(msg.getRole());
            userMsg.setContent(msg.getContent());
            userMsg.setCreatedAt(new Date());
            chatRepo.save(userMsg);
        }
        

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

            ChatMessageOpenAI assistantMsg = new ChatMessageOpenAI();
            assistantMsg.setUserId(chatRequest.getUserId());
            assistantMsg.setRole("assistant");
            assistantMsg.setContent(reply);
            assistantMsg.setCreatedAt(new Date());
            chatRepo.save(assistantMsg);

            return ResponseEntity.ok(new ChatResponseDTO(reply));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponseDTO("Error calling OpenAI: " + e.getMessage()));
        }
    }

    @GetMapping("/chat/history")
    public ResponseEntity<List<ChatMessageOpenAI>> getChatHistory() {
        List<ChatMessageOpenAI> messages = chatRepo.findAll();
        messages.sort(Comparator.comparing(ChatMessageOpenAI::getCreatedAt));
    return ResponseEntity.ok(messages);
}

}
