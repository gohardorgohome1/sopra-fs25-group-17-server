package ch.uzh.ifi.hase.soprafs24.rest.dto;

import java.util.List;

public class ChatRequestDTO {
    public static class Message {
        public String role;
        public String content;
    }

    private List<Message> messages;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
