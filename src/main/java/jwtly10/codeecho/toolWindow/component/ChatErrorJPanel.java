package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class ChatErrorJPanel extends JPanel {

    public ChatErrorJPanel(String errorMessage) {
        init(errorMessage);
    }

    private void init(String errorMessage) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createLineBorder(JBColor.RED, 1));

        revalidate();
        repaint();

        JPanel errorPanel = new JPanel();
        errorPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel errorLabel = new JLabel(errorMessage);
        errorLabel.setForeground(JBColor.BLACK);
        errorLabel.setBackground(JBColor.PINK);

        errorPanel.add(errorLabel);
        mainPanel.add(errorPanel);

        mainPanel.revalidate();
        mainPanel.repaint();

        add(mainPanel);
        revalidate();
        repaint();
    }
}
