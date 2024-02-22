package jwtly10.codeecho.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChatSession {
    private final String id;
    private final List<Message> messages = new ArrayList<>();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastUpdated;

    public ChatSession() {
        this.id = UUID.randomUUID().toString();
    }

    public List<Message> getMessages() {
        return messages;
    }

    public String getId() {
        return id;
    }

    public void addMessage(Message message) {
        messages.add(message);
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public void setLastUpdatedNow() {
        this.lastUpdated = LocalDateTime.now();
    }
}
