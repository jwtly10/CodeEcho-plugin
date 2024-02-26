package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.components.JBScrollPane;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.toolWindow.component.ChatErrorJPanel;
import jwtly10.codeecho.toolWindow.component.MessageJPanel;
import jwtly10.codeecho.toolWindow.component.StreamMessageJPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessageWindowJPanel extends JPanel {
    private final JPanel messagesPanel = new JPanel();
    private final JPanel innerMessagesPanel = new JPanel();
    private Component filler = Box.createVerticalGlue();

    public MessageWindowJPanel() {
    }

    public void initialLoad(List<ChatGPTMessage> messages) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        setOpaque(false);

        messagesPanel.setLayout(new BorderLayout());
        innerMessagesPanel.setLayout(new BoxLayout(innerMessagesPanel, BoxLayout.Y_AXIS));

        for (ChatGPTMessage message : messages) {
            if (message.getContent().trim().isEmpty()) {
                ChatErrorJPanel errorComponent = new ChatErrorJPanel("There was an error generating this response, please try again.");
                innerMessagesPanel.add(errorComponent);
            } else {
                MessageJPanel messageComponent = new MessageJPanel(message);
                innerMessagesPanel.add(messageComponent);
            }
        }

        messagesPanel.add(innerMessagesPanel, BorderLayout.NORTH);
        messagesPanel.add(filler, BorderLayout.CENTER);

        JBScrollPane scrollPane = new JBScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();

        scrollToBottom();
    }

    public void addNewMessage(ChatGPTMessage message) {
        if (filler != null) {
            messagesPanel.remove(filler);
        }

        MessageJPanel messageComponent = new MessageJPanel(message);
        innerMessagesPanel.add(messageComponent);

        filler = Box.createVerticalGlue();
        messagesPanel.add(filler, BorderLayout.CENTER);

        revalidate();
        repaint();

        scrollToBottom();
    }

    public void removeOldestMessage() {
        Component[] components = innerMessagesPanel.getComponents();
        if (components.length > 0) {
            innerMessagesPanel.remove(components[0]);
            innerMessagesPanel.revalidate();
            innerMessagesPanel.repaint();
        }
    }

    public void addNewErrorMessage(String errorMessage) {
        if (filler != null) {
            messagesPanel.remove(filler);
        }

        ChatErrorJPanel errorComponent = new ChatErrorJPanel(errorMessage);
        innerMessagesPanel.add(errorComponent);

        filler = Box.createVerticalGlue();
        messagesPanel.add(filler);

        revalidate();
        repaint();

        scrollToBottom();
    }


    public void addNewStreamComponent(StreamMessageJPanel streamMessageComponent) {
        if (filler != null) {
            messagesPanel.remove(filler);
        }

        innerMessagesPanel.add(streamMessageComponent);

        filler = Box.createVerticalGlue();
        messagesPanel.add(filler);

        revalidate();
        repaint();
    }

    public void scrollToBottom() {
        if (messagesPanel.getParent() instanceof JViewport viewport) {
            SwingUtilities.invokeLater(() -> {
                viewport.scrollRectToVisible(new Rectangle(0, messagesPanel.getHeight() - 1, 1, 1));
            });
        }
    }


}