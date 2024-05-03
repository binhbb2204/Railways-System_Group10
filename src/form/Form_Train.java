
package form;
import model.Model_Card;
import model.TrainType;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import swing.AddingActionEvent;
import swing.ScrollBar;
import javax.swing.JScrollPane;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JPanel;

public class Form_Train extends javax.swing.JPanel {
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
    public void insertTrainDataToDatabase(String trainID, String trainName, int trainCapacity, String trainType){
        String query = "INSERT INTO railway_system.train (trainID, trainName, trainCapacity, trainType) VALUES (?, ?, ?, ?)";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, trainID);
            pstmt.setString(2, trainName);
            pstmt.setInt(3, trainCapacity);
            pstmt.setString(4, trainType);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }
    public void populateTrainTable(){
        String query = "SELECT trainID, trainName, trainCapacity, trainType FROM railway_system.train";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    String trainID = rs.getString("trainID");
                    String trainName = rs.getString("trainName");
                    int trainCapacity = rs.getInt("trainCapacity");
                    String trainTypeString = rs.getString("trainType");
                    //IF that column is a combo box, we need to convert it back to combo box by using the code below
                    TrainType trainType = TrainType.valueOf(trainTypeString);
                    model.addRow(new Object[]{trainID, trainName, trainCapacity, trainType});
                }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void deleteTrainDataFromDatabase(String trainID) {
        String query = "DELETE FROM railway_system.train WHERE trainID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void updateTrainDataInDatabase(String trainID, String newTrainName, int newTrainCapacity, String newTrainType) {
        String query = "UPDATE railway_system.train SET trainName = ?, trainCapacity = ?, trainType = ? WHERE trainID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newTrainName);
            pstmt.setInt(2, newTrainCapacity);
            pstmt.setString(3, newTrainType);
            pstmt.setString(4, trainID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // JOptionPane.showMessageDialog(this, "Error updating data: " + e.getMessage());
            // e.printStackTrace();
        }
    }

    private boolean checkIfTrainIdExists(String trainID) {
        // Implement the logic to check if the train ID exists in the database
        // Return true if it exists, false otherwise
        // This method needs to query the database and return the result
        // Example implementation:
        String query = "SELECT COUNT(*) FROM railway_system.train WHERE trainID = ?";
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
    public Form_Train() {

        initComponents();
        updateTotalPassengerCountDisplay();
        AddingActionEvent event1 = new AddingActionEvent() {
            @Override
            public void onAdding(int row) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"", "", "", TrainType.SE});
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
                String trainID = model.getValueAt(row, 0).toString();
                
                // Delete data from the database
                deleteTrainDataFromDatabase(trainID);
                model.removeRow(row);
                updateTotalPassengerCountDisplay();
            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String id = model.getValueAt(row, 0).toString();
                String name = model.getValueAt(row, 1).toString();
                int capacity = Integer.parseInt(model.getValueAt(row, 2).toString());
                String type = model.getValueAt(row, 3).toString();
                if (checkIfTrainIdExists(id)) {
                    // Train ID exists, so update the record
                    updateTrainDataInDatabase(id, name, capacity, type);
                } else {
                    // Train ID does not exist, so insert a new record
                    insertTrainDataToDatabase(id, name, capacity, type);
                }
                table.repaint();
                table.revalidate();
                updateTotalPassengerCountDisplay();
                
            }
            
            
            
            
        };
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(event));


        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        //card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", "131,227", "increased by 5%"));


        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        //this one is used to make the status into combo box
        JComboBox<TrainType> comboBox = new JComboBox<>(TrainType.values());
        table.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(comboBox));
        // table.addRow(new Object[]{"HN-SG-123", "SE3 34h22", "400", TrainType.SE});
        // table.addRow(new Object[]{"HN-HP-113", "SE5 37h00", "700", TrainType.TN});
        // table.addRow(new Object[]{"HN-HP-113", "SE5 37h00", "500", TrainType.TN});

        // Retrieve and populate data from the database
        populateTrainTable();
  

    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        panel = new javax.swing.JLayeredPane();
        card1 = new component.Card();
        card2 = new component.Card();
        card3 = new component.Card();
        panelBorder1 = new swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new swing.TrainTable();
        cmdAdding = new swing.AddingRowPanelAction();
        jLabel2 = new javax.swing.JLabel();

        jTextField1.setText("jTextField1");

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
        jLabel1.setText("Train Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Train ID", "Train Name", "Capacity", "Train Type", "Action"
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
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdAdding, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
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
    private javax.swing.JTextField jTextField1;
    private javax.swing.JLayeredPane panel;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane spTable;
    private swing.TrainTable table;
    // End of variables declaration//GEN-END:variables
}
