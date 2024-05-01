package swing;


import javax.swing.*;
import model.CoachType;
import java.awt.*;

public class CoachStatus extends JLabel{
    private CoachType type;

    public CoachStatus(){
        setForeground(Color.WHITE);
    }

    public CoachType getType(){
        return type;
    }

    public void setType(CoachType type){
        this.type = type;
        setText(type.toString());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (type != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp;
            if (type == CoachType.VERY_HARD_SEAT || type == CoachType.HARD_SEAT || type == CoachType.HARD_SLEEPER) {
                gp = new GradientPaint(0, 0, Color.decode("#44A08D"), 0, getHeight(), Color.decode("#43C6AC")); //green
            } 
            else if (type == CoachType.POWER_CAR || type == CoachType.DINING_CAR || type == CoachType.BAGGAGE_VAN) {
                gp = new GradientPaint(0, 0, Color.decode("#642B73"), 0, getHeight(), Color.decode("#C6426E"));
            }
            else if (type == CoachType.SOFT_SEAT || type == CoachType.SOFT_SLEEPER) {
                gp = new GradientPaint(0, 0, Color.decode("#2F80ED"), 0, getHeight(), Color.decode("#56CCF2")); //blue
            }
            else{ //double deck coach
                gp = new GradientPaint(0, 0, Color.decode("#00B4DB"), 0, getHeight(), Color.decode("#0083B0"));
            }
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);
        }
        super.paintComponent(g);
    }
}
