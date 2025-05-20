package ch.uzh.ifi.hase.soprafs24.rest.dto;
import java.util.List;

public class ChatInviteDTO {
    private String fromUserId;
    private List<String> toUserIds;

    // Getters and setters
    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public List<String> getToUserIds() {
        return toUserIds;
    }

    public void setToUserIds(List<String> toUserIds) {
        this.toUserIds = toUserIds;
    }
}
