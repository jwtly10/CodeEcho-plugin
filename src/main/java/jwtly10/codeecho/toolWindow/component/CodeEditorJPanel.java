package jwtly10.codeecho.toolWindow.component;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class CodeEditorJPanel extends JPanel {

    RSyntaxTextArea codeBlock;

    private static final Logger log = Logger.getInstance(CodeEditorJPanel.class);

    public CodeEditorJPanel() {
        codeBlock = new RSyntaxTextArea();
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        codeBlock.setAntiAliasingEnabled(true);
        codeBlock.setEditable(false);
        codeBlock.setLineWrap(true);
        codeBlock.setBackground(JBColor.background());
        codeBlock.setActiveLineRange(0, 0);
        codeBlock.setHighlightCurrentLine(false);
        codeBlock.setBracketMatchingEnabled(false);
        codeBlock.setMargin(JBUI.insets(5));

        RTextScrollPane scrollPane = new RTextScrollPane(codeBlock);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setWheelScrollingEnabled(false);

        scrollPane.setLineNumbersEnabled(true);

        scrollPane.addMouseWheelListener(e -> {
            if (!scrollPane.isWheelScrollingEnabled()) {
                e.getComponent().getParent().dispatchEvent(e);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    public void set(String code, String language) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (UIManager.getLookAndFeel().getName().contains("Light")) {
                    Theme theme = Theme.load(getClass().getResourceAsStream(
                            "/themes/code/idea.xml"));
                    theme.apply(codeBlock);
                } else {
                    Theme theme = Theme.load(getClass().getResourceAsStream(
                            "/themes/code/dark.xml"));
                    theme.apply(codeBlock);
                }

//                codeBlock.setBackground(JBColor.background());
            } catch (Exception e) {
                log.error("Failed to load theme, defaulting", e);
            }
        });
        code = code.strip();
        String lang = switch (language) {
            case "java" -> SyntaxConstants.SYNTAX_STYLE_JAVA;
            case "rust" -> SyntaxConstants.SYNTAX_STYLE_RUST;
            case "javascript" -> SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT;
            case "go" -> SyntaxConstants.SYNTAX_STYLE_GO;
            case "python" -> SyntaxConstants.SYNTAX_STYLE_PYTHON;
            case "c" -> SyntaxConstants.SYNTAX_STYLE_C;
            case "c++" -> SyntaxConstants.SYNTAX_STYLE_CPLUSPLUS;
            case "c#" -> SyntaxConstants.SYNTAX_STYLE_CSHARP;
            case "kotlin" -> SyntaxConstants.SYNTAX_STYLE_KOTLIN;
            case "typescript" -> SyntaxConstants.SYNTAX_STYLE_TYPESCRIPT;
            case "html" -> SyntaxConstants.SYNTAX_STYLE_HTML;
            case "css" -> SyntaxConstants.SYNTAX_STYLE_CSS;
            case "xml" -> SyntaxConstants.SYNTAX_STYLE_XML;
            case "json" -> SyntaxConstants.SYNTAX_STYLE_JSON;
            case "yaml" -> SyntaxConstants.SYNTAX_STYLE_YAML;
            case "sql" -> SyntaxConstants.SYNTAX_STYLE_SQL;
            case "shell", "bash" -> SyntaxConstants.SYNTAX_STYLE_UNIX_SHELL;
            default -> SyntaxConstants.SYNTAX_STYLE_NONE;
        };

        codeBlock.setSyntaxEditingStyle(lang);
        codeBlock.setText(code);

        codeBlock.revalidate();
        codeBlock.repaint();

        revalidate();
        repaint();
    }
}
