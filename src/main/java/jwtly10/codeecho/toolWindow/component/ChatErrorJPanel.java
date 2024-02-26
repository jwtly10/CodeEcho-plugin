package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class ChatErrorJPanel extends JPanel {

    public ChatErrorJPanel(String errorMessage) {
        setLayout(new BorderLayout());
        init(errorMessage);
    }

    private void init(String errorMessage) {
        JTextPane errorLabel = new JTextPane();
        errorLabel.setEditable(false);
        errorLabel.setText(errorMessage);
        errorLabel.setForeground(JBColor.RED);
        add(errorLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}
