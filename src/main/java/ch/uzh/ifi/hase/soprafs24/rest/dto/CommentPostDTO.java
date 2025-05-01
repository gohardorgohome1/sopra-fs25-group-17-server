package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class CommentPostDTO {

    private String userId;
    private String message;

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}