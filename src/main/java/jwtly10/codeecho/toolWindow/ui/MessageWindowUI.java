package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.components.JBScrollPane;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessageWindowUI extends JPanel {

    public MessageWindowUI() {
    }

    public void set(List<ChatGPTMessage> messages) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        setOpaque(false);

        JPanel messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));

        for (ChatGPTMessage message : messages) {
            JPanel messageContainer = createMessageContainer(message);
            messagesPanel.add(messageContainer);
        }

        JBScrollPane scrollPane = new JBScrollPane(messagesPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        add(scrollPane, BorderLayout.CENTER);

        revalidate();
        repaint();

        // Schedule the scroll to bottom action after all UI updates are done
        SwingUtilities.invokeLater(() -> {
            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            verticalScrollBar.setValue(verticalScrollBar.getMaximum());
        });
    }

    @NotNull
    private static JPanel createMessageContainer(ChatGPTMessage message) {
        JPanel messageContainer = new JPanel();
        FlowLayout flowLayout = (message.getRole().equals(ChatGPTRole.user)) ? new FlowLayout(FlowLayout.RIGHT) : new FlowLayout(FlowLayout.LEFT);
        messageContainer.setLayout(flowLayout);

        MessageComponent messageComponent = new MessageComponent(message);
        messageContainer.add(messageComponent);
        return messageContainer;
    }
}