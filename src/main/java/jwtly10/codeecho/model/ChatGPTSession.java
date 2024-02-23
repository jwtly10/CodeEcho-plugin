package jwtly10.codeecho.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatGPTSession {
    private final String id;
    private final List<ChatGPTMessage> messages = new ArrayList<>();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    public ChatGPTSession() {
        this.id = UUID.randomUUID().toString();
    }

    public List<ChatGPTMessage> getMessages() {
        return messages;
    }

    public String getId() {
        return id;
    }

    public void addMessage(ChatGPTMessage message) {
        messages.add(message);
        setLastUpdatedNow();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    private void setLastUpdatedNow() {
        this.lastUpdated = LocalDateTime.now();
    }
}
