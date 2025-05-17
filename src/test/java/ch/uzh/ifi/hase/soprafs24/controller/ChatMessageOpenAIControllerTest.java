package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessageOpenAI;
import ch.uzh.ifi.hase.soprafs24.repository.ChatMessageOpenAIRepository;
import ch.uzh.ifi.hase.soprafs24.repository.ChatGroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.ChatRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatMessageOpenAIController.class)
public class ChatMessageOpenAIControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatMessageOpenAIRepository chatRepo;

    @MockBean
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetChatHistory_returnsSortedMessages() throws Exception {
        ChatMessageOpenAI msg1 = new ChatMessageOpenAI();
        msg1.setContent("First");
        msg1.setCreatedAt(new Date(System.currentTimeMillis() - 1000));

        ChatMessageOpenAI msg2 = new ChatMessageOpenAI();
        msg2.setContent("Second");
        msg2.setCreatedAt(new Date());

        when(chatRepo.findAll()).thenReturn(Arrays.asList(msg2, msg1));

        mockMvc.perform(get("/openai/chat/history"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("First"))
                .andExpect(jsonPath("$[1].content").value("Second"));
    }
    
    //@Disabled("Disabled due to missing or invalid OpenAI API key")
    /*@Disabled("Disabled due to mocking or restTemplate not working")
    @Test
    void chatWithOpenAI_returnsAssistantMessage() throws Exception {
        // Arrange test data
        ChatRequestDTO.Message message = new ChatRequestDTO.Message();
        message.setRole("user");
        message.setContent("Hello!");

        ChatRequestDTO chatRequest = new ChatRequestDTO();
        chatRequest.setUserId("user1");
        chatRequest.setUsername("alex");
        chatRequest.setMessages(List.of(message));

        // Mock OpenAI API response structure
        Map<String, Object> responseMessage = new HashMap<>();
        responseMessage.put("content", "Hi there!");

        Map<String, Object> choice = new HashMap<>();
        choice.put("message", responseMessage);

        Map<String, Object> openAIResponse = new HashMap<>();
        openAIResponse.put("choices", List.of(choice));

        // Mock restTemplate manually using spy (Spring won't inject here, but we simulate)
        RestTemplate restTemplate = spy(new RestTemplate());
        doReturn(ResponseEntity.ok(openAIResponse))
            .when(restTemplate).postForEntity(anyString(), any(), eq(Map.class));

        // Capture saved messages
        ArgumentCaptor<ChatMessageOpenAI> messageCaptor = ArgumentCaptor.forClass(ChatMessageOpenAI.class);

        // Act
        mockMvc.perform(post("/openai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").value("Hi there!"));

        // Assert two messages saved (user + assistant)
        verify(chatRepo, times(2)).save(messageCaptor.capture());

        List<ChatMessageOpenAI> savedMessages = messageCaptor.getAllValues();
        assertThat(savedMessages.get(0).getContent()).isEqualTo("Hello!");
        assertThat(savedMessages.get(1).getContent()).isEqualTo("Hi there!");
    }*/

    //@Disabled("Disabled due to missing or invalid OpenAI API key")
    @Test
    void chatWithOpenAI_handlesApiFailureGracefully() throws Exception {
        // Arrange input
        ChatRequestDTO.Message message = new ChatRequestDTO.Message();
        message.setRole("user");
        message.setContent("Hello!");

        ChatRequestDTO chatRequest = new ChatRequestDTO();
        chatRequest.setUserId("user1");
        chatRequest.setUsername("alex");
        chatRequest.setMessages(List.of(message));

        // Simulate a server failure (OpenAI throws an exception)
        // You don't need to mock the RestTemplate here – it'll fail naturally or you can refactor into a service

        mockMvc.perform(post("/openai/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(chatRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.reply").value(org.hamcrest.Matchers.containsString("Error calling OpenAI")));
    }
}