package jwtly10.codeecho.toolWindow.ui;

import com.intellij.ui.components.JBScrollPane;
import jwtly10.codeecho.callback.AsyncCallback;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MessageWindowUI extends JPanel {
    private final JPanel messagesPanel = new JPanel();
    private Component filler = Box.createVerticalGlue();

    public MessageWindowUI() {
    }

    public void set(List<ChatGPTMessage> messages) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        removeAll();
        setOpaque(false);

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
        messagesPanel.add(filler, gbc);

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
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        if (message.getRole().equals(ChatGPTRole.user)) {
            gbc.anchor = GridBagConstraints.LINE_END;
        } else {
            gbc.anchor = GridBagConstraints.LINE_START;
        }


        if (filler != null) {
            messagesPanel.remove(filler);
        }

        MessageComponent messageComponent = new MessageComponent(message);
        messagesPanel.add(messageComponent, gbc);

        gbc.weighty = 1;
        filler = Box.createVerticalGlue();
        messagesPanel.add(filler, gbc);

        messagesPanel.revalidate();
        messagesPanel.repaint();

        scrollToBottom();
    }

    public void streamNewMessage(JTextArea streamTextArea, AsyncCallback<ChatGPTMessage> callback) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 0;

        gbc.anchor = GridBagConstraints.LINE_START;


        if (filler != null) {
            messagesPanel.remove(filler);
        }

        MessageComponent messageComponent = new MessageComponent(streamTextArea, callback);
        messagesPanel.add(messageComponent, gbc);

        gbc.weighty = 1;
        filler = Box.createVerticalGlue();
        messagesPanel.add(filler, gbc);

        messagesPanel.revalidate();
        messagesPanel.repaint();

        scrollToBottom();
    }

    private void scrollToBottom() {
        if (messagesPanel.getParent() instanceof JViewport viewport) {
            SwingUtilities.invokeLater(() -> {
                viewport.scrollRectToVisible(new Rectangle(0, messagesPanel.getHeight() - 1, 1, 1));
            });
        }
    }


}