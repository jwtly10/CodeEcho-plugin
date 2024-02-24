package jwtly10.codeecho.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ChatGPTMessage {
    // TODO: Limit context messages
    /*
     * TODO: Implement a clean up method to remove old messages
     *  Or some logic to limit the number of messages in the chat window for perf
     *  could split data up into multiple files, and only load whats needed
     */

    private ChatGPTRole role;
    private String content;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    @JsonCreator
    public ChatGPTMessage(
            @JsonProperty("role") ChatGPTRole role,
            @JsonProperty("content") String content,
            @JsonProperty("timestamp") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime timestamp) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public ChatGPTMessage(ChatGPTRole role, String content) {
        this.role = role;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public ChatGPTRole getRole() {
        return role;
    }

    public void setRole(ChatGPTRole role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

}
