
package form;

import java.awt.Color;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import model.Model_Card;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionEvent;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import javax.swing.JTable;

public class Form_Ticket extends javax.swing.JPanel {
    private boolean editable = false;
    private int editableRow = -1;



    private void updateTotalPassengerCountDisplay() {
        // Retrieve the total passenger count from the PassengerManager
        int count = PassengerManager.getInstance().getTotalPassengers();
        // Format the total count and update the card display
        String formattedTotal = String.format("%,d", count);
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", formattedTotal, "increased by 5%"));
    }
    public void onSwitchBackToSchedule() {
        updateTotalPassengerCountDisplay();
    }

    public Form_Ticket() {
        initComponents();

        // AddingActionEvent event1 = new AddingActionEvent() {
        //     @Override
        //     public void onAdding(int row) {
        //         DefaultTableModel model = (DefaultTableModel) table.getModel();
        //         model.addRow(new Object[]{"", "", "", "", "", "", "", "", "", "", ""});
        //         model.fireTableDataChanged();
        //         updateTotalPassengerCountDisplay();
        //     }
        // };
        TableActionEvent event = new TableActionEvent() {
        @Override
        public void onEdit(int row) {
            editableRow = row;
            editable = true;
            ((DefaultTableModel)table.getModel()).fireTableDataChanged();

            table.repaint();
            table.revalidate();
            updateTotalPassengerCountDisplay();
        }
        @Override
        public void onDelete(int row) {
            if(table.isEditing()) {
               table.getCellEditor().stopCellEditing();
            }
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            String ticketID = model.getValueAt(row, 0).toString();

            //deleteTicketDataFromDatabase(ticketID);
            model.removeRow(row);
            updateTotalPassengerCountDisplay();
        }
        public void onView(int row) {
            editableRow = row;
            editable = false;
            ((DefaultTableModel)table.getModel()).fireTableDataChanged();
            updateTotalPassengerCountDisplay();
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            String ticketID = model.getValueAt(row, 0).toString();
            int passengerID = Integer.parseInt(model.getValueAt(row, 1).toString());
            String trainID = model.getValueAt(row, 2).toString();
            String coachID = model.getValueAt(row, 3).toString();
            String seatID = model.getValueAt(row, 4).toString();
            String departure_stationID = model.getValueAt(row, 5).toString();
            String arrival_stationID = model.getValueAt(row, 6).toString();
            String departureTime = model.getValueAt(row, 7).toString();
            String departureDate = model.getValueAt(row, 8).toString();
            String ticketPrice = model.getValueAt(row, 9).toString();
            //String ticketStatus = model.getValueAt(row, 10).toString();

            //if (checkIfPassengerIdExists()) {
            //    Passenger ID exist, update the record
            // ...
            //    Passenger ID dne, insert a new record
            //...
            
            table.repaint();
            table.revalidate();
            updateTotalPassengerCountDisplay();
            //populateTicketTable();
        
        }



    };
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(event));
        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        updateTotalPassengerCountDisplay();
        //card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", "₫ 9,112,001,000", "increased by 5%"));
        
        //add row table
        sPTable.setVerticalScrollBar(new ScrollBar());
        sPTable.getVerticalScrollBar().setBackground(Color.WHITE);
        sPTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        //if Ticket Status is a JComboBox
        //sPTable.getColumnModel().getColumn(10).setCellEditor(new DefaultCellEditor(new JComboBox<>(ticketStatus.values())));
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelBorder1 = new swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        sPTable = new javax.swing.JScrollPane();
        table = new swing.TicketTable();
        panel = new javax.swing.JLayeredPane();
        card1 = new component.Card();
        card2 = new component.Card();
        card3 = new component.Card();

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 126, 126));
        jLabel1.setText("Ticket Table");

        sPTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Ticket ID", "Passenger ID", "Train ID", "Coach ID", "Seat ID", "Departure Station ID", "Arrival Station ID", "Departure Time", "Departure Date", "Ticket Price"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 9){
                    return true;
                }
                return rowIndex == editableRow && editable;
            }
        });
        sPTable.setViewportView(table);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sPTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, 0)))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sPTable, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Card card1;
    private component.Card card2;
    private component.Card card3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane panel;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane sPTable;
    private swing.TicketTable table;
    // End of variables declaration//GEN-END:variables
}
