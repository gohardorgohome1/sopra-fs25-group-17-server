package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatGroup;
import ch.uzh.ifi.hase.soprafs24.entity.ChatMessageOpenAI;
import ch.uzh.ifi.hase.soprafs24.repository.ChatGroupRepository;
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

    @PostMapping("/helper")
    public ResponseEntity<ChatResponseDTO> correctExoplanetName(@RequestBody String exoplanetName) {
        RestTemplate restTemplate = new RestTemplate();


        String userPrompt = String.format(
    "You are an Exoplanet Name corrector. You must provide the correct name for a given exoplanet. " +
    "You must take into account that the letter of the planet after the name of the Host Star has to have a blank space of separation. " +
    "You must also be careful about uppercase and lowercase letters. " +
    "You must provide names of exoplanets that exist, as the user could give you an invented one!" +
    "An example of your task: Given X0-5b as an input, you should return the corrected name as XO-5 b. " +
    "Some common star names names that are prone to be spelled wrong are the following and you should correct are: CoRoT, TrES, XO, HAT-P-1, WASP, GJ, HD, HAT-P-2, OGLE-TR-113, etc " +
    "Your answer has to be given in this strict form: \"AI Assistance: This exoplanet name is wrong! Were you thinking about this exoplanet? <exoplanet name corrected provided by the AI>. Please try again.\" " +
    "The name of the exoplanet that you have to correct is %s.",
    exoplanetName
        );


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini-2024-07-18");

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "user", "content", userPrompt));
        requestBody.put("messages", messages);

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


    

    @PostMapping("/chat")
public ResponseEntity<ChatResponseDTO> chatWithOpenAI(@RequestBody ChatRequestDTO chatRequest) {
    RestTemplate restTemplate = new RestTemplate();

    for (ChatRequestDTO.Message msg : chatRequest.getMessages()) {
        ChatMessageOpenAI userMsg = new ChatMessageOpenAI();
        userMsg.setUserId(chatRequest.getUserId());
        userMsg.setSenderName(chatRequest.getUsername());
        userMsg.setGroupId(chatRequest.getGroupId());
        userMsg.setRole(msg.getRole());
        userMsg.setContent(msg.getContent());
        userMsg.setCreatedAt(new Date());
        chatRepo.save(userMsg);
    }


    if (!chatRequest.isAiEnabled()) {
        return ResponseEntity.ok(new ChatResponseDTO(null)); 
    }

    Map<String, String> systemMessage = new HashMap<>();
    systemMessage.put("role", "system");
    systemMessage.put("content", "You are an exoplanet, astrophysicist and cosmology expert. Your task is to answer questions about science and respond accurately. However you must not respond to questions that does not have to do anything with exoplanets, physics, space or science and you should indicate the user that you are a customized exoplanets expert and you must answer only questions about that. Do not be too long wth your answers.");

    List<Map<String, String>> openAIMessages = new ArrayList<>();
    openAIMessages.add(systemMessage);

    for (ChatRequestDTO.Message msg : chatRequest.getMessages()) {
        Map<String, String> message = new HashMap<>();
        message.put("role", msg.getRole());
        message.put("content", msg.getContent());
        openAIMessages.add(message);
    }

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-4o-mini-2024-07-18");
    requestBody.put("messages", openAIMessages);

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
        assistantMsg.setGroupId(chatRequest.getGroupId());
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

    @Autowired
    private ChatGroupRepository chatGroupRepo;

    @PostMapping("/chat/group")
    public ResponseEntity<ChatGroup> createGroup(@RequestBody ChatGroup group) {
        group.setCreatedAt(new Date());
        ChatGroup saved = chatGroupRepo.save(group);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/chat/groups/{userId}")
    public ResponseEntity<List<ChatGroup>> getGroupsForUser(@PathVariable String userId) {
        return ResponseEntity.ok(chatGroupRepo.findByUserIdsContaining(userId));
    }

    @GetMapping("/chat/history/{groupId}")
    public ResponseEntity<List<ChatMessageOpenAI>> getGroupHistory(@PathVariable String groupId) {
        List<ChatMessageOpenAI> messages = chatRepo.findByGroupId(groupId);
        messages.sort(Comparator.comparing(ChatMessageOpenAI::getCreatedAt));
        return ResponseEntity.ok(messages);
    }

    @DeleteMapping("/chat/group/{groupId}")
    public ResponseEntity<Void> deleteGroup(@PathVariable String groupId) {
        chatRepo.deleteByGroupId(groupId);
        chatGroupRepo.deleteById(groupId);
        return ResponseEntity.ok().build();
}







}
