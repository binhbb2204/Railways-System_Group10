package swing;

import javax.swing.*;
import java.awt.*;

public class PanelCircle extends JPanel {
    private final int diameter = 30;

    public PanelCircle() {
        setOpaque(false);
        setPreferredSize(new Dimension(diameter, diameter));
        setMinimumSize(new Dimension(diameter, diameter));
        setMaximumSize(new Dimension(diameter, diameter));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255));
        g2.fillOval(0, 0, diameter, diameter);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(diameter, diameter);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(diameter, diameter);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(diameter, diameter);
    }
}