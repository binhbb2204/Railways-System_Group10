package swing;

import java.awt.Color;

import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import model.StatusType;

public class TableStatus extends JPanel {

    private StatusType statusType;
    
    private JComboBox<StatusType> statusCombo;

    public TableStatus() {
        statusType = StatusType.ON_TIME;
        
        // Get all enum constants and create a combo box with them
        StatusType[] statuses = StatusType.values();
        statusCombo = new JComboBox<>(statuses);
        statusCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                statusType = (StatusType) statusCombo.getSelectedItem();
                repaint();
            }
        });
        add(statusCombo);
    }

    public StatusType getType() {
        return this.statusType;
    }

    public void setType(StatusType type) {
        this.statusType = type;
        statusCombo.setSelectedItem(type);
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        super.paintComponent(g); // Paint the default component first
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Define color strings for the gradient
        String topColorCode;
        String bottomColorCode;

        // Determine the colors based on the StatusType
        if (getType().equals(StatusType.ON_TIME)) {
            topColorCode = "#38ef7d"; 
            bottomColorCode = "#11998e"; 
        } else if (getType().equals(StatusType.DELAYED)) {
            topColorCode = "#e65c00"; 
            bottomColorCode = "#F9D423"; 
        } else if (getType().equals(StatusType.CANCELLED)) {
            topColorCode = "#e52d27"; 
            bottomColorCode = "#b31217"; 
        } else {
            topColorCode = "#02AAB0"; // White
            bottomColorCode = "#00CDAC"; 
        }

        // Decode the color strings to Color objects
        Color topColor = Color.decode(topColorCode);
        Color bottomColor = Color.decode(bottomColorCode);

        // Create a gradient paint with the two colors
        GradientPaint gp = new GradientPaint(0, 0, topColor, 0, getHeight(), bottomColor);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);
        
        g2.dispose();
    }
}
