package jwtly10.codeecho.toolWindow.component;

import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomButton extends JButton {
    private final int radius = 20;
    private JBColor color;
    private JBColor defaultColor;

    public CustomButton(Icon icon, JBColor initialColor) {
        super(icon);
        this.color = initialColor;
        this.defaultColor = initialColor;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                color = (JBColor) color.darker();
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                color = defaultColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(color);
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
        super.paintComponent(g);
    }

    public void setCurrentColor(JBColor newColor) {
        this.color = newColor;
        this.defaultColor = newColor;
        repaint();
    }

    public void disableBtn() {
        this.setEnabled(false);
        this.color = JBColor.GRAY;
        repaint();
    }

    public void enableBtn() {
        this.setEnabled(true);
        this.color = defaultColor;
        repaint();
    }

    public void updateIcon(Icon newIcon) {
        setIcon(newIcon);
        repaint();
    }

}