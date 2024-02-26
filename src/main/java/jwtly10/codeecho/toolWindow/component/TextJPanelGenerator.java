package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;
import jwtly10.codeecho.model.SectionType;
import jwtly10.codeecho.service.ParserService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/* This class is responsible for generating a JPanel that dynamically contains text and code blocks.
 * This class handles all parsing and formatting of the type of content */
public class TextJPanelGenerator extends JPanel {
    CodeEditorJPanel codeEditorJPanel;

    ParserService parserService = new ParserService();
    JPanel horizontalPanel = new JPanel();

    public TextJPanelGenerator() {
        setLayout(new BorderLayout());
        init();
        SwingUtilities.invokeLater(() -> {
            setOpaque(false);
            setBackground(JBColor.background());
        });
    }

    private void init() {
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.Y_AXIS));
        codeEditorJPanel = new CodeEditorJPanel();
    }

    public void setText(String message) {
        horizontalPanel.removeAll();
        List<SectionType> sections = parserService.parseMarkdownIntoSections(message);

        for (int i = 0; i < sections.size(); i++) {
            switch (sections.get(i).getType()) {
                case CODE -> {
                    codeEditorJPanel.set(sections.get(i).getContent(), sections.get(i).getLanguage());
                    horizontalPanel.add(codeEditorJPanel);
                    SwingUtilities.invokeLater(() -> {
                        horizontalPanel.setOpaque(false);
                        horizontalPanel.setBackground(JBColor.background());
                        codeEditorJPanel.setOpaque(false);
                        codeEditorJPanel.setBackground(JBColor.background());
                    });
                }
                case TEXT -> {
                    JTextPane textPane = new JTextPane();
                    SwingUtilities.invokeLater(() -> {
                        horizontalPanel.setOpaque(false);
                        horizontalPanel.setBackground(JBColor.background());
                        textPane.setOpaque(false);
                        textPane.setBackground(JBColor.background());
                        textPane.setFont(new Font("Arial", Font.PLAIN, 15));
                    });
                    textPane.setContentType("text/html");
                    textPane.setEditable(false);
                    textPane.setText(sections.get(i).getContent());
                    horizontalPanel.add(textPane);
                }
            }
            // Create some space between content
            if (i < sections.size() - 1) {
                horizontalPanel.add(Box.createVerticalStrut(10));
            }
        }

        codeEditorJPanel.revalidate();
        codeEditorJPanel.repaint();

        add(horizontalPanel, BorderLayout.CENTER);
    }
}
