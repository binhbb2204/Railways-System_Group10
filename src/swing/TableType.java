package swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.*;

import model.TrainType; // Make sure this import statement is correct

public class TableType extends JLabel{

    private TrainType type;
    public TableType() {
        setForeground(Color.WHITE);
    }

    public TrainType getType() {
        return this.type;
    }

    public void setType(TrainType type) {
        this.type = type;
        setText(type.getDescription());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(type != null){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Define color strings for the gradient
            String topColorCode;
            String bottomColorCode;

            // Determine the colors based on the TrainType
            switch (getType()) {
                case SE:
                    topColorCode = "#396afc";
                    bottomColorCode = "#2948ff";
                    break;
                case SP:
                    topColorCode = "#1c92d2";
                    bottomColorCode = "#f2fcfe";
                    break;
                case TN:
                    topColorCode = "#0f9b0f";
                    bottomColorCode = "#45a247";
                    break;
                case FIVE_STAR: // Add this case
                    topColorCode = "#e67e22"; // Example color code
                    bottomColorCode = "#f1c40f"; // Example color code
                    break;
                default:
                    topColorCode = "#2F80ED"; // Default color
                    bottomColorCode = "#56CCF2";
                break;
            }

            // Decode the color strings to Color objects
            Color topColor = Color.decode(topColorCode);
            Color bottomColor = Color.decode(bottomColorCode);

            // Create a gradient paint with the two colors
            GradientPaint gp = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        
        
        super.paintComponent(g); // Paint the default component first
    }
}
