package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;
import jwtly10.codeecho.model.ChatGPTMessage;
import jwtly10.codeecho.model.ChatGPTRole;
import jwtly10.codeecho.toolWindow.utils.CColor;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class MessageJPanel extends JPanel {

    public MessageJPanel(ChatGPTMessage message) {
        setLayout(new BorderLayout());
        init(message);
    }

    private void init(ChatGPTMessage msg) {
        JPanel metaPanel = new JPanel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = msg.getTimestamp().format(formatter);
        JLabel timeLabel = new JLabel(formattedDate);
        String role = msg.getRole().equals(ChatGPTRole.user) ? "You" : "GPT";
        JLabel userLabel = new JLabel(role);

        JPanel msgPanel = new JPanel();

        switch (msg.getRole()) {
            case user:
                msgPanel = createUserJPanel();
                metaPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
                metaPanel.add(timeLabel);
                metaPanel.add(userLabel);
                JPanel finalMsgPanel = msgPanel;
                SwingUtilities.invokeLater(() -> {
                    finalMsgPanel.setOpaque(false);
                    finalMsgPanel.setBackground(CColor.PURPLE);
                });
                break;
            case system:
//                metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
//                metaPanel.add(userLabel);
//                metaPanel.add(timeLabel);
//                SwingUtilities.invokeLater(() -> {
//                    msgPanel.setOpaque(true);
//                });
                break;
        }

        msgPanel.setLayout(new BorderLayout());
        msgPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        TextJPanelGenerator msgContent = new TextJPanelGenerator();
        SwingUtilities.invokeLater(() -> {
            msgContent.setBackground(JBColor.background());
        });
        msgContent.setText(msg.getContent());
        msgPanel.add(msgContent, BorderLayout.CENTER);

        add(metaPanel, BorderLayout.NORTH);
        add(msgPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private JPanel createUserJPanel() {
        return new JPanel() {
            private final int radius = 20;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
            }

            @Override
            protected void paintBorder(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
                g2.dispose();
            }
        };
    }

}
