package jwtly10.codeecho.toolWindow.utils;

import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;

import java.awt.*;

/**
 * A class that holds custom colors, used to create consistency between light and dark mode for certain components
 */
public class CColor {
    public static final JBColor BLUE = new JBColor(new Color(78, 120, 204), new Color(78, 120, 204));
    public static final JBColor RED = new JBColor(new Color(201, 79, 78), new Color(201, 79, 78));
    public static final JBColor GREEN = new JBColor(new Color(89, 158, 95), new Color(89, 158, 95));
    public static final JBColor PURPLE = new JBColor(new Color(194, 195, 255), new Color(65, 69, 168));
    public static final JBColor DARK_GREY = new JBColor(new Color(33, 38, 42), new Color(33, 38, 42));
    public static final JBColor TRANSPARENT = new JBColor(new Color(255, 255, 255, 0), new Color(255, 255, 255, 0));

    public static final JBColor BLACK = new JBColor(Gray._0, Gray._255);
}
