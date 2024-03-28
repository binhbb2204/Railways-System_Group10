
package component;

import model.StatusType;
import javax.swing.*;
import java.awt.*;

public class ComboStatus extends javax.swing.JPanel {

    public ComboStatus() {
        initComponents();
        
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusCombo = new javax.swing.JComboBox<>();

        statusCombo.setModel(new javax.swing.DefaultComboBoxModel<>(StatusType.values()));
        

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(statusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(statusCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        StatusType selectedStatus = (StatusType) statusCombo.getSelectedItem();

        if (selectedStatus != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp;
            switch (selectedStatus) {
                case ON_TIME:
                    gp = new GradientPaint(0, 0, Color.decode("#38ef7d"), 0, getHeight(), Color.decode("#11998e"));
                    break;
                case DELAYED:
                    gp = new GradientPaint(0, 0, Color.decode("#fc4a1a"), 0, getHeight(), Color.decode("#fc4a1a"));
                    break;
                default:
                    gp = new GradientPaint(0, 0, Color.decode("#EB5757"), 0, getHeight(), Color.decode("#000000"));
                    break;
            }

            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 1, 1);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<StatusType> statusCombo;
    // End of variables declaration//GEN-END:variables
}
