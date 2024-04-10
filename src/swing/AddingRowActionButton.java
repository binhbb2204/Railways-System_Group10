package swing;

import javax.swing.JButton;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AddingRowActionButton extends JButton {

    private boolean mousePress;
    private Color pressedColor = new Color(158, 158, 158);
    private Color releasedColor = new Color(255, 255, 255); // Set to white

    public AddingRowActionButton() {
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(3, 3, 3, 3));
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mousePress = true;
                repaint(); // Repaint immediately on press
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                mousePress = false;
                repaint(); // Repaint immediately on release
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int x = (width - size) / 2;
        int y = (height - size) / 2;
        if(mousePress){
            g2.setColor(pressedColor);
        }
        else{
            g2.setColor(releasedColor);
        }
        g2.fill(new Rectangle2D.Double(x, y, size, size));
        g2.dispose();
        super.paintComponent(g); // Call the superclass method at the end
    }
}
