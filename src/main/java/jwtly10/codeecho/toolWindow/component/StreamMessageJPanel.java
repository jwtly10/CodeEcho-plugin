package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/* This class is an alternate implementation of MessageJPanel.java
 *  It exposes the TextJPanelGenerator, which allows updating from outside the class */
public class StreamMessageJPanel extends JPanel {

    TextJPanelGenerator streamMessageArea = new TextJPanelGenerator();

    public void setText(String msg) {
        streamMessageArea.setText(msg);
        revalidate();
        repaint();
    }

    public void setHidden(boolean visible) {
        setVisible(!visible);
    }

    public StreamMessageJPanel() {
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        JPanel metaPanel = new JPanel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = now.format(formatter);
        JLabel timeLabel = new JLabel(formattedDate);
        JLabel userLabel = new JLabel("GPT");
        metaPanel.add(userLabel);
        metaPanel.add(timeLabel);
        metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JPanel msgPanel = new JPanel();
        msgPanel.setLayout(new BorderLayout());
        Border border1 = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border border2 = BorderFactory.createLineBorder(JBColor.BLACK, 1);
        msgPanel.setBorder(BorderFactory.createCompoundBorder(border2, border1));
        msgPanel.add(streamMessageArea, BorderLayout.CENTER);

        add(metaPanel, BorderLayout.NORTH);
        add(msgPanel, BorderLayout.CENTER);

        revalidate();
        repaint();
    }
}
