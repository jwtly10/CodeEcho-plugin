package jwtly10.codeecho.model;

import java.util.List;

public class ChatGPTRequest {

    public List<ChatGPTMessage> messages;
    public String msg;

    public ChatGPTRequest(List<ChatGPTMessage> messages, String msg) {
        this.messages = messages;
        this.msg = msg;
    }

    public List<ChatGPTMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatGPTMessage> messages) {
        this.messages = messages;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
