
package form;

import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import swing.ScrollBar;
import model.Model_Card;
import model.StatusType;

public class Form_Home extends javax.swing.JPanel {

    public Form_Home() {
        initComponents();
        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", "131,227", "increased by 5%"));
        
        //add row table
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        //table.getColumModel is used for the status column because it's a JComboBox
        table.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JComboBox<>(StatusType.values())));
        table.addRow(new Object[]{"SE3 34h22", "Hà Nội Station", "Biên Hòa Station", "22/03/2024 07:20pm", " 23/03/2024 05:33am", "Monday - Sunday", StatusType.ON_TIME});
        table.addRow(new Object[]{"SE3 34h22", "Biên Hòa Station", "Sài Gòn Station", "23/03/2024 05:36pm", " 23/03/2024 06:30am", "Monday - Sunday", StatusType.DELAYED });
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        table.addRow(new Object[]{"SE5 37h00", "Hà Nội Station", "Đà Nẵng Station", "22/03/2024 03:30pm", " 23/03/2024 08:26am", "Monday - Sunday", StatusType.CANCELLED});
        
        
    }

  
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JLayeredPane();
        card1 = new component.Card();
        card2 = new component.Card();
        card3 = new component.Card();
        panelBorder1 = new swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new swing.Table();

        panel.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        card1.setColor1(new java.awt.Color(0, 102, 255));
        card1.setColor2(new java.awt.Color(102, 153, 255));
        panel.add(card1);

        card2.setColor1(new java.awt.Color(186, 123, 247));
        card2.setColor2(new java.awt.Color(167, 94, 236));
        panel.add(card2);

        card3.setColor1(new java.awt.Color(51, 153, 0));
        card3.setColor2(new java.awt.Color(102, 204, 0));
        panel.add(card3);

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Standard Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Train", "Origin", "Destination", "Departure Time", "Arrival Time", "Day Operation", "Status"
            }
        ));
        spTable.setViewportView(table);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(spTable))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Card card1;
    private component.Card card2;
    private component.Card card3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane panel;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane spTable;
    private swing.Table table;
    // End of variables declaration//GEN-END:variables
}
