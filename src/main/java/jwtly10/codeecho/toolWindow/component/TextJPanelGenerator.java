package jwtly10.codeecho.toolWindow.component;

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
    }

    private void init() {
        horizontalPanel.setLayout(new BoxLayout(horizontalPanel, BoxLayout.Y_AXIS));
        codeEditorJPanel = new CodeEditorJPanel();
    }

    public void setText(String message) {
        horizontalPanel.removeAll();
        List<SectionType> sections = parserService.parseMarkdownIntoSections(message);

        for (SectionType section : sections) {
            switch (section.getType()) {
                case CODE -> {
                    codeEditorJPanel.set(section.getContent(), section.getLanguage());
                    horizontalPanel.add(codeEditorJPanel);
                }
                case TEXT -> {
                    JTextPane textPane = new JTextPane();
                    textPane.setContentType("text/html");
                    textPane.setEditable(false);
                    textPane.setText(section.getContent());
                    horizontalPanel.add(textPane);
                }
            }
        }

        codeEditorJPanel.revalidate();
        codeEditorJPanel.repaint();

        add(horizontalPanel, BorderLayout.CENTER);
    }
}
