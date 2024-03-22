
package swing;

import java.awt.*;
import javax.swing.*;

import model.StatusType;

public class TableStatus extends JLabel{
    private StatusType type;
    public TableStatus(){
        setForeground(Color.WHITE);
    }

    public StatusType getType(){
        return type;
    }

    public void setType(StatusType type){
        this.type = type;
        setText(type.toString());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(type != null){
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp;
            if(type == StatusType.ON_TIME){
                gp = new GradientPaint(0, 0, Color.decode("#38ef7d"), 0, getHeight(), Color.decode("#11998e"));
            }
            else if(type == StatusType.DELAYED){
                gp = new GradientPaint(0, 0, Color.decode("#fc4a1a"), 0, getHeight(), Color.decode("#f7b733"));
            }
            else{
                gp = new GradientPaint(0, 0, Color.decode("#EB5757"), 0, getHeight(), Color.decode("#000000"));
            }
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);
        }
        super.paintComponent(g);
    }
}
