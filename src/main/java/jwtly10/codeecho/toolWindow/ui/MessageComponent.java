package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.JBColor;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;
import jwtly10.codeecho.service.ParserService;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageComponent extends JPanel {

    public MessageComponent(ChatGPTMessage message) {
        init(message);
    }

    public MessageComponent(JTextPane streamTextPane, AsyncCallback<ChatGPTMessage> callback) {
        stream(streamTextPane, callback);
    }

    private void stream(JTextPane streamTextPane, AsyncCallback<ChatGPTMessage> callback) {
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
        Border border1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border border2 = BorderFactory.createLineBorder(JBColor.BLACK, 1);
        msgPanel.setBorder(BorderFactory.createCompoundBorder(border2, border1));
        streamTextPane.setEditable(false);
        streamTextPane.setContentType("text/html");
        metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        metaPanel.add(userLabel);
        metaPanel.add(timeLabel);
        msgPanel.add(streamTextPane);

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
        Border border1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border border2 = BorderFactory.createLineBorder(JBColor.BLACK, 1);
        msgPanel.setBorder(BorderFactory.createCompoundBorder(border2, border1));

        JTextPane msgContent = new JTextPane();
        String htmlContent = ParserService.markdownToHtml(msg.getContent());
        msgContent.setPreferredSize(new Dimension(600, getContentHeight(600, htmlContent)));
        msgContent.setContentType("text/html");
        msgContent.setEditable(false);

        msgContent.setText(htmlContent);
        switch (msg.getRole()) {
            case user:
                metaPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                metaPanel.add(timeLabel);
                metaPanel.add(userLabel);
                break;
            case system:
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

    public static int getContentHeight(int width, String htmlContent) {
        JEditorPane dummyEditorPane = new JEditorPane();
        dummyEditorPane.setSize(width, Short.MAX_VALUE);
        dummyEditorPane.setContentType("text/html");
        dummyEditorPane.setText(htmlContent);

        return dummyEditorPane.getPreferredSize().height;
    }
}
