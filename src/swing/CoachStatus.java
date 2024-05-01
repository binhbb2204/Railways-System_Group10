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
        setText(type.getDescription());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (type != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp;
            if (type == CoachType.VERY_HARD_SEAT || type == CoachType.HARD_SEAT || type == CoachType.HARD_SLEEPER) {
                gp = new GradientPaint(0, 0, Color.decode("#237A57"), 0, getHeight(), Color.decode("#44A08D")); //green
            } 
            else if (type == CoachType.POWER_CAR || type == CoachType.DINING_CAR || type == CoachType.BAGGAGE_VAN) {
                gp = new GradientPaint(0, 0, Color.decode("#E44D26"), 0, getHeight(), Color.decode("#F16529"));//orange
            }
            else if (type == CoachType.SOFT_SEAT || type == CoachType.SOFT_SLEEPER) {
                gp = new GradientPaint(0, 0, Color.decode("#0575E6"), 0, getHeight(), Color.decode("#021B79")); //blue
            }
            else{ //double deck coach
                gp = new GradientPaint(0, 0, Color.decode("#F2994A"), 0, getHeight(), Color.decode("#F2C94C"));//yellow
            }
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
        }
        super.paintComponent(g);
    }
}
