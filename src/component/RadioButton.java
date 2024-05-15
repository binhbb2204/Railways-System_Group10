package component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RadioButton extends JRadioButton {

    public RadioButton() {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setBackground(new Color(150, 150, 150));
        setFocusPainted(false);
        setIcon(null);
    }

    @Override
    protected void paintComponent(Graphics grphcs) {
        super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        double size = Math.min(width, height) - 10;
        double x = (height - size) / 2.0;
        double y = (height - size) / 2.0;

        // Draw the outer circle
        g2.setColor(getBackground());
        g2.fillOval((int) x, (int) y, (int) size, (int) size);

        // Draw the inner circle if selected
        if (isSelected()) {
            g2.setColor(getForeground());
            double innerSize = size / 2.0;
            double innerX = x + (size - innerSize) / 2.0;
            double innerY = y + (size - innerSize) / 2.0;
            g2.fillOval((int) innerX, (int) innerY, (int) innerSize, (int) innerSize);
        }

        // // Draw the text
        // FontMetrics fm = g2.getFontMetrics();
        // int textX = (int) (x + size + 10);
        // int textY = (height - fm.getHeight()) / 2 + fm.getAscent();
        // g2.setColor(getForeground());
        // g2.drawString(getText(), textX, textY);

        // g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        // Optionally, draw a custom border
    }
}