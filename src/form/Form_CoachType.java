
package form;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import connection.ConnectData;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.CoachType;
import model.Model_Card;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;


public class Form_CoachType extends javax.swing.JPanel {
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

//SQL JDBC
//-----------------------------------------------------------------------------------------------------
    public void insertCoachTypeDataToDatabase(String coach_typeID, String type, int price, int capacity){
        String query = "INSERT INTO railway_system.coach_type (coach_typeID, type, price, capacity) VALUES (?, ?, ?, ?)";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, coach_typeID);
            pstmt.setString(2, type);
            pstmt.setInt(3, price);
            pstmt.setInt(4, capacity);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            // e.printStackTrace();
            // JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    public void populateCoachTypeTable(){
        String query = "SELECT coach_typeID, type, price, capacity FROM railway_system.coach_type";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    String coach_typeID = rs.getString("coach_typeID");
                    String coach_type = rs.getString("type");
                    int price = rs.getInt("price");
                    int capacity = rs.getInt("capacity");
                    //convert it back to combo bõ
                    CoachType coachtype = CoachType.valueOf(coach_type);
                    model.addRow(new Object[]{coach_typeID, coachtype, price, capacity});
                    
                }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteCoachTypeDataFromDatabase(String coach_typeID) {
        String query = "DELETE FROM railway_system.coach_type WHERE coach_typeID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, coach_typeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateCoachTypeDataInDatabase(String coach_typeID, String newType, int newPrice, int capacity) {
        String query = "UPDATE railway_system.coach_type SET type = ?, price = ?, capacity = ? WHERE coach_typeID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newType);
            pstmt.setInt(2, newPrice);
            pstmt.setInt(3, capacity);
            pstmt.setString(4, coach_typeID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + e.getMessage());
            e.printStackTrace();
        }
    }
        private boolean checkIfCoachTypeIdExists(String trainID) {
            // Implement the logic to check if the train ID exists in the database
            // Return true if it exists, false otherwise
            // This method needs to query the database and return the result
            // Example implementation:
            String query = "SELECT COUNT(*) FROM railway_system.coach_type WHERE coach_typeID = ?";
            try (Connection conn = new ConnectData().connect();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, trainID);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        return count > 0;
                    }
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error checking if ID exists: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
    }
//-----------------------------------------------------------------------------------------------------          

    public Form_CoachType() {
        initComponents();

        updateTotalPassengerCountDisplay();

        AddingActionEvent event1 = new AddingActionEvent() {
            @Override
            public void onAdding(int row) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"", CoachType.HARD_SEAT, "", ""});
                model.fireTableDataChanged();
                updateTotalPassengerCountDisplay();
            }
            
        };
        cmdAdding.initEvent(event1, 0);

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
                String coach_typeID = (String) model.getValueAt(row, 0);
                deleteCoachTypeDataFromDatabase(coach_typeID);
                model.removeRow(row);
                updateTotalPassengerCountDisplay();
                
            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                updateTotalPassengerCountDisplay();
                DefaultTableModel model = (DefaultTableModel) table.getModel(); 
                String coach_typeID = model.getValueAt(row, 0).toString();
                String type = model.getValueAt(row, 1).toString();
                int price = Integer.parseInt(model.getValueAt(row, 2).toString());
                int capacity = Integer.parseInt(model.getValueAt(row, 3).toString());
                if (checkIfCoachTypeIdExists(type)) {
                    //update the record
                    updateCoachTypeDataInDatabase(coach_typeID, type, price, capacity);
                } else {
                    //insert new record
                    insertCoachTypeDataToDatabase(coach_typeID, type, price, capacity);
                }
                updateTotalPassengerCountDisplay();
                table.repaint();
                table.revalidate();
                populateCoachTypeTable();
                
            }
            
            
        };
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(event));

        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));

        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JComboBox<>(CoachType.values())));
        //table.addRow(new Object[]{"CT01", CoachType.HARD_SEAT, "250000"});
        //table.addRow(new Object[]{"CT02", CoachType.SOFT_SLEEPER, "250000"});
        
        populateCoachTypeTable();
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
        table = new swing.CoachTypeTable();
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
        jLabel1.setText("Coach Type Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Coach Type ID", "Type", "Price", "Capacity", "Action"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 4){
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
                    .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
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
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmdAdding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                .addGap(24, 24, 24))
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
    private swing.CoachTypeTable table;
    // End of variables declaration//GEN-END:variables
}
