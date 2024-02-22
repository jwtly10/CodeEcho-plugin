package jwtly10.codeecho.model;

public class ChatGPTContext {
    private ChatGPTRole role;
    private String content;

    public ChatGPTContext(ChatGPTRole role, String content) {
        this.role = role;
        this.content = content;
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

}
