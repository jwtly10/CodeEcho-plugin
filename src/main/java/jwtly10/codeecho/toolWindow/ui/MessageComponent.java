package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.JBColor;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageComponent extends JPanel {

    public MessageComponent(ChatGPTMessage message) {
        init(message);
    }

    public MessageComponent(JTextArea streamTextArea, AsyncCallback<ChatGPTMessage> callback) {
        stream(streamTextArea, callback);
    }

    private void stream(JTextArea streamTextArea, AsyncCallback<ChatGPTMessage> callback) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        LocalDateTime now = LocalDateTime.now();

        JPanel metaPanel = new JPanel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        JLabel timeLabel = new JLabel(formattedDate);

        JLabel userLabel = new JLabel("GPT");

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBorder(BorderFactory.createLineBorder(JBColor.BLACK, 1));

        streamTextArea.setWrapStyleWord(true);
        streamTextArea.setLineWrap(true);
        streamTextArea.setEditable(false);
        streamTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        streamTextArea.setColumns(60);
        metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        metaPanel.add(userLabel);
        metaPanel.add(timeLabel);
        msgPanel.add(streamTextArea);

        mainPanel.add(metaPanel);
        mainPanel.add(msgPanel);

        add(mainPanel);

        revalidate();
        repaint();
    }

    private void init(ChatGPTMessage msg) {

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel metaPanel = new JPanel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = msg.getTimestamp().format(formatter);
        JLabel timeLabel = new JLabel(formattedDate);


        String role = msg.getRole().equals(ChatGPTRole.user) ? "You" : "GPT";
        JLabel userLabel = new JLabel(role);

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BoxLayout(msgPanel, BoxLayout.Y_AXIS));
        msgPanel.setBorder(BorderFactory.createLineBorder(JBColor.BLACK, 1));

        JTextArea msgContent = new JTextArea();
        msgContent.setWrapStyleWord(true);
        msgContent.setLineWrap(true);
        msgContent.setEditable(false);
        msgContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        switch (msg.getRole()) {
            case user:
                msgContent.setText(msg.getContent());
                msgContent.setColumns(60);
                metaPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                metaPanel.add(timeLabel);
                metaPanel.add(userLabel);
                break;
            case system:
                msgContent.setText(msg.getContent());
                msgContent.setColumns(60);
                metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
                metaPanel.add(userLabel);
                metaPanel.add(timeLabel);
                break;
        }
        msgPanel.add(msgContent);


        mainPanel.add(metaPanel);
        mainPanel.add(msgPanel);

        add(mainPanel);

        revalidate();
        repaint();
    }
}
