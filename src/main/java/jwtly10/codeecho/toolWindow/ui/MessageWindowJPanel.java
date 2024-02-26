package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.JBColor;
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
    private Component filler = Box.createVerticalGlue();

    public MessageWindowJPanel() {
    }

    public void initialLoad(List<ChatGPTMessage> messages) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        setOpaque(false);

        messagesPanel.setLayout(new BorderLayout());



        JPanel innerMessagesPanel = new JPanel();
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

    public void removeOldestMessage() {
        Component[] components = messagesPanel.getComponents();
        if (components.length > 0) {
            messagesPanel.remove(components[0]);
            messagesPanel.revalidate();
            messagesPanel.repaint();
        }
    }

    public void addNewErrorMessage(String errorMessage) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        if (filler != null) {
            messagesPanel.remove(filler);
        }

        ChatErrorJPanel errorComponent = new ChatErrorJPanel(errorMessage);
        messagesPanel.add(errorComponent, gbc);

        filler = Box.createVerticalGlue();
        gbc.weighty = 1;
        messagesPanel.add(filler, gbc);

        messagesPanel.revalidate();
        messagesPanel.repaint();

        scrollToBottom();
    }

    public void addNewMessage(ChatGPTMessage message) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        if (filler != null) {
            messagesPanel.remove(filler);
        }

        MessageJPanel messageComponent = new MessageJPanel(message);
        messagesPanel.add(messageComponent, gbc);

        filler = Box.createVerticalGlue();
        gbc.weighty = 1;
        messagesPanel.add(filler, gbc);

        messagesPanel.revalidate();
        messagesPanel.repaint();

        scrollToBottom();
    }

    public void addNewStreamComponent(StreamMessageJPanel streamMessageComponent) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        if (filler != null) {
            messagesPanel.remove(filler);
        }

        messagesPanel.add(streamMessageComponent, gbc);

        filler = Box.createVerticalGlue();
        gbc.weighty = 1;
        messagesPanel.add(filler, gbc);

        messagesPanel.revalidate();
        messagesPanel.repaint();

        scrollToBottom();
    }

    public void scrollToBottom() {
        if (messagesPanel.getParent() instanceof JViewport viewport) {
            SwingUtilities.invokeLater(() -> {
                viewport.scrollRectToVisible(new Rectangle(0, messagesPanel.getHeight() - 1, 1, 1));
            });
        }
    }


}