package jwtly10.codeecho.toolWindow.component;

import javax.swing.*;

public class ComponentSharedUtils {
    public static int getContentHeight(int width, String htmlContent) {
        JEditorPane dummyEditorPane = new JEditorPane();
        dummyEditorPane.setSize(width, Short.MAX_VALUE);
        dummyEditorPane.setContentType("text/html");
        dummyEditorPane.setText(htmlContent);

        return dummyEditorPane.getPreferredSize().height;
    }
}
