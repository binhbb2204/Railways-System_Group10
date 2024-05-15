
package form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import connection.ConnectData;
import model.Model_Card;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;
import java.awt.*;
public class Form_Seat extends javax.swing.JPanel {
    private boolean editable = false;
    private int editableRow = -1;

    private void updateTotalPassengerCountDisplay() {
        // Retrieve the total passenger count from the PassengerManager
        int count = PassengerManager.getInstance().getTotalPassengers();
        // Format the total count and update the card display
        String formattedTotal = String.format("%,d", count);
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", formattedTotal, "increased by 5%"));
    }
    public void onSwitchBackToSeat() {
        updateTotalPassengerCountDisplay();
    }
//SQL JDBC
//-----------------------------------------------------------------------------------------------------
public void insertSeatDataToDatabase(String coachID, int seatNumber, DefaultTableModel model) {
    String query = "INSERT INTO railway_system.seat (coachID, seatNumber) SELECT ?, ? FROM DUAL " +
                   "WHERE NOT EXISTS (SELECT 1 FROM railway_system.seat WHERE coachID = ? AND seatNumber = ?)";

    try (Connection conn = new ConnectData().connect();
         PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
        pstmt.setString(1, coachID);
        pstmt.setInt(2, seatNumber);
        pstmt.setString(3, coachID);
        pstmt.setInt(4, seatNumber);

        int affectedRows = pstmt.executeUpdate();

        if (affectedRows > 0) {
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int seatID = generatedKeys.getInt(1);
                    // Add row to UI table model for the new seat
                    model.addRow(new Object[]{seatID, coachID, seatNumber});
                }
            }
        } else {
            // The seat already exists, so retrieve the existing seatID
            String selectExistingSeatId = "SELECT seatID FROM railway_system.seat WHERE coachID = ? AND seatNumber = ?";
            try (PreparedStatement selectStmt = conn.prepareStatement(selectExistingSeatId)) {
                selectStmt.setString(1, coachID);
                selectStmt.setInt(2, seatNumber);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        int existingSeatID = rs.getInt("seatID");
                        // Add row to UI table model for the existing seat
                        model.addRow(new Object[]{existingSeatID, coachID, seatNumber});
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
public void populateSeatTable() {
    String selectTrainSql = "SELECT trainID, trainName, coachTotal FROM railway_system.train";

    try (Connection conn = new ConnectData().connect();
         Statement stmt = conn.createStatement()) {

        // Clear existing rows in the UI table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);

        // Get trains and their coach totals
        try (ResultSet rs = stmt.executeQuery(selectTrainSql)) {
            while (rs.next()) {
                String trainID = rs.getString("trainID");
                String trainName = rs.getString("trainName");
                int coachTotal = rs.getInt("coachTotal");

                // Get coaches for the train
                String selectCoachesSql = "SELECT c.coachID, c.coach_typeID, ct.capacity " +
                                           "FROM railway_system.coach c " +
                                           "JOIN railway_system.coach_type ct ON c.coach_typeID = ct.coach_typeID " +
                                           "WHERE c.trainID = ?";
                
                try (PreparedStatement pstmt = conn.prepareStatement(selectCoachesSql)) {
                    pstmt.setString(1, trainID);
                    try (ResultSet coachesResultSet = pstmt.executeQuery()) {
                        while (coachesResultSet.next()) {
                            String coachID = coachesResultSet.getString("coachID");
                            String coachTypeID = coachesResultSet.getString("coach_typeID");
                            int capacity = coachesResultSet.getInt("capacity");
                            
                            // Insert seats for the coach
                            for (int seatNumber = 1; seatNumber <= capacity; seatNumber++) {
                                insertSeatDataToDatabase(coachID, seatNumber, model);
                            }
                        }
                    }
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}





//-----------------------------------------------------------------------------------------------------
    public Form_Seat() {
        initComponents();



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
                if(table.isEditing()){
                    table.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.removeRow(row);
                
            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                updateTotalPassengerCountDisplay();
                table.repaint();
                table.revalidate();
                
            }
            
            
        };
        table.getColumnModel().getColumn(3).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(3).setCellEditor(new TableActionCellEditor(event));

        
        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        updateTotalPassengerCountDisplay();
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        // table.addRow(new Object[]{"Seat01", "02", "10"});
        // table.addRow(new Object[]{"Seat02", "02", "11"});
        // table.addRow(new Object[]{"Seat03", "02", "12"});
        populateSeatTable();
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
        table = new swing.SeatTable();
        cmdAdding = new swing.AddingRowPanelAction();
        jLabel2 = new javax.swing.JLabel();

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
        jLabel1.setText("Seat Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Seat ID", "Coach ID", "Seat Number", "Action"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 3){
                    return true;
                }
                return rowIndex == editableRow && editable;
            }
        });
        spTable.setViewportView(table);

        cmdAdding.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("Add Row");

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 829, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdAdding, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdAdding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spTable)
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
                    .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Card card1;
    private component.Card card2;
    private component.Card card3;
    private swing.AddingRowPanelAction cmdAdding;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane panel;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane spTable;
    private swing.SeatTable table;
    // End of variables declaration//GEN-END:variables
}
