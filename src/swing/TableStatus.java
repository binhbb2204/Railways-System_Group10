package swing;

import java.awt.Color;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.*;

import model.StatusType;

public class TableStatus extends JLabel{

    private StatusType type;
    

    public TableStatus() {
        setForeground(Color.WHITE);
    }

    public StatusType getType() {
        return type;
    }

    public void setType(StatusType type) {
        this.type = type;
        setText(type.toString());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(type != null){
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Define color strings for the gradient
            String topColorCode;
            String bottomColorCode;
            GradientPaint gp;
            // Determine the colors based on the StatusType
                if (type == StatusType.ON_TIME) {
                    topColorCode = "#38ef7d"; 
                    bottomColorCode = "#11998e"; 
                    gp = new GradientPaint(0, 0, Color.decode(topColorCode), 0, getHeight(), Color.decode(bottomColorCode));
                } 
                else if (type == StatusType.DELAYED) {
                    topColorCode = "#e65c00"; 
                    bottomColorCode = "#F9D423"; 
                    gp = new GradientPaint(0, 0, Color.decode(topColorCode), 0, getHeight(), Color.decode(bottomColorCode));
                } 
                else if (type == StatusType.CANCELLED) {
                    topColorCode = "#e52d27"; 
                    bottomColorCode = "#b31217"; 
                    gp = new GradientPaint(0, 0, Color.decode(topColorCode), 0, getHeight(), Color.decode(bottomColorCode));
                } 
                else {
                    topColorCode = "#02AAB0"; // White
                    bottomColorCode = "#00CDAC"; 
                    gp = new GradientPaint(0, 0, Color.decode(topColorCode), 0, getHeight(), Color.decode(bottomColorCode));
                }
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        super.paintComponent(g); // Paint the default component first
    }
}
