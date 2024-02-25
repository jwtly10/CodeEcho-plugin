package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StreamMessageJPanel extends JPanel {

    JTextPane streamTextArea = new JTextPane();

    public void setText(String htmlContent) {
        streamTextArea.setContentType("text/html");
        streamTextArea.setText(htmlContent);
        streamTextArea.setPreferredSize(new Dimension(600, ComponentSharedUtils.getContentHeight(600, htmlContent)));
        revalidate();
        repaint();
    }

    public void setHidden(boolean visible) {
        setVisible(!visible);
    }

    public StreamMessageJPanel() {
        init();
    }

    private void init() {
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
        streamTextArea.setEditable(false);
        streamTextArea.setContentType("text/html");
        metaPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        metaPanel.add(userLabel);
        metaPanel.add(timeLabel);

        msgPanel.add(streamTextArea);

        mainPanel.add(metaPanel);
        mainPanel.add(msgPanel);

        add(mainPanel);

        revalidate();
        repaint();
    }
}
