package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.components.JBScrollPane;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;

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
        messagesPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        for (ChatGPTMessage message : messages) {
            MessageComponent messageComponent = new MessageComponent(message);

            if (message.getRole().equals(ChatGPTRole.user)) {
                gbc.anchor = GridBagConstraints.LINE_END;
            } else {
                gbc.anchor = GridBagConstraints.LINE_START;
            }
            messagesPanel.add(messageComponent, gbc);
        }

        gbc.weighty = 1;
        Component filler = Box.createVerticalGlue();
        messagesPanel.add(filler, gbc);

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
}