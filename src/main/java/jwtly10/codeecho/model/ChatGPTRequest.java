package jwtly10.codeecho.model;

import java.util.List;

public class ChatGPTRequest {

    public List<ChatGPTContext> messages;
    public String msg;

    public ChatGPTRequest(List<ChatGPTContext> messages, String msg) {
        this.messages = messages;
        this.msg = msg;
    }

    public List<ChatGPTContext> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatGPTContext> messages) {
        this.messages = messages;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
