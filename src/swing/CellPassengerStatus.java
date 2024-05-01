
package swing;

import model.PassengerStatus;

public class CellPassengerStatus extends javax.swing.JPanel {

    public CellPassengerStatus(PassengerStatus type) {
        initComponents();
        tablePassengerStatus1.setType(type);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tablePassengerStatus1 = new swing.TablePassengerStatus();

        setBackground(new java.awt.Color(255, 255, 255));

        tablePassengerStatus1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        tablePassengerStatus1.setText("tablePassengerStatus1");
        tablePassengerStatus1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(10, Short.MAX_VALUE)
                .addComponent(tablePassengerStatus1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(tablePassengerStatus1, javax.swing.GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private swing.TablePassengerStatus tablePassengerStatus1;
    // End of variables declaration//GEN-END:variables
}
