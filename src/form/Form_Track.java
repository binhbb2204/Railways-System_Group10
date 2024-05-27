package form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import connection.ConnectData;
import model.Model_Card;
import model.TrainType;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;


public class Form_Track extends javax.swing.JPanel {
    private boolean editable = false;
    private int editableRow = -1; 
    private Form_Ticket ticketForm;
    private Form_Passenger passengerForm;



//SQL JDBC
//-----------------------------------------------------------------------------------------------------
    public void insertTrackDataToDatabase(String trackID, String station1ID, String station2ID, int distance){
        String query = "INSERT INTO railway_system.track (trackID, station1ID, station2ID, distance) VALUES (?, ?, ?, ?)";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, trackID);
            pstmt.setString(2, station1ID);
            pstmt.setString(2, station2ID);
            pstmt.setInt(4, distance);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }
    public void populateTrackTable(){
        String query = "SELECT trackID, station1ID, station2ID, distance FROM railway_system.track";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                while(rs.next()){
                    String trackID = rs.getString("trackID");
                    String station1ID = rs.getString("station1ID");
                    String station2ID = rs.getString("station2ID");
                    int distance = rs.getInt("distance");
                    //IF that column is a combo box, we need to convert it back to combo box by using the code below
    
                    model.addRow(new Object[]{trackID, station1ID, station2ID, distance});
                }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void deleteTrackDataFromDatabase(String trackID) {
        String query = "DELETE FROM railway_system.track WHERE trackID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trackID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void updateTrackDataInDatabase(String trackID, String station1ID, String station2ID, int distance) {
        String query = "UPDATE railway_system.track SET station1ID = ?, station2ID = ?, distance = ? WHERE stationID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, station1ID);
            pstmt.setString(2, station2ID);
            pstmt.setInt(3, distance);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private boolean checkIfTrackIdExists(String trackID) {
        // Implement the logic to check if the train ID exists in the database
        // Return true if it exists, false otherwise
        // This method needs to query the database and return the result
        // Example implementation:
        String query = "SELECT COUNT(*) FROM railway_system.track WHERE trackID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trackID);
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
    public Form_Track() {
        initComponents();
        ticketForm = new Form_Ticket();
        int totalProfit = ticketForm.populateTotalTicketPrice();
        
        passengerForm = new Form_Passenger();
        int totalPassenger = passengerForm.populateTotalPassenger();
        
        AddingActionEvent event1 = new AddingActionEvent() {
            @Override
            public void onAdding(int row) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"", "", "", ""});
                model.fireTableDataChanged();

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

                
                
            }
            @Override
            public void onDelete(int row) {
                if(table.isEditing()){
                    table.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String trackID = model.getValueAt(row, 0).toString();
                
                // Delete data from the database
                deleteTrackDataFromDatabase(trackID);
                //deleteTrainDataFromDatabase(trainID);
                model.removeRow(row);

            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String trackID = model.getValueAt(row, 0).toString();
                String station1ID = model.getValueAt(row, 1).toString();
                String station2ID = model.getValueAt(row, 2).toString();
                int distance = Integer.parseInt(model.getValueAt(row, 3).toString());
                if(checkIfTrackIdExists(trackID)){
                    updateTrackDataInDatabase(trackID, station1ID, station2ID, distance);
                }
                else{
                    insertTrackDataToDatabase(trackID, station1ID, station2ID, distance);
                }
                table.repaint();
                table.revalidate();

                populateTrackTable();
            }
            
            
            
            
        };
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(event));

        String formattedProfit = String.format("₫ %,d", totalProfit);
        String formattedPassenger = String.format("%,d", totalPassenger);
        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", formattedProfit, "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", formattedPassenger, "increased by 5%"));
        //add row table
        
        sPTable.setVerticalScrollBar(new ScrollBar());
        sPTable.getVerticalScrollBar().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        sPTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        // table.addRow(new Object[]{"1", "Ha Noi", "Sai Gon"});
        // table.addRow(new Object[]{"2", "Sai Gon", "Phu Yen"});
        populateTrackTable();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel = new javax.swing.JLayeredPane();
        card1 = new component.Card();
        card2 = new component.Card();
        card3 = new component.Card();
        panelBorder1 = new swing.PanelBorder();
        jLabel4 = new javax.swing.JLabel();
        cmdAdding = new swing.AddingRowPanelAction();
        jLabel5 = new javax.swing.JLabel();
        sPTable = new javax.swing.JScrollPane();
        table = new swing.TrainTable();

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

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(127, 127, 127));
        jLabel4.setText("Track Table Design");

        cmdAdding.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(127, 127, 127));
        jLabel5.setText("Add Row");

        sPTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Track ID", "Station 1", "Station 2", "Distance (km)","Action"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 4){
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
                    .addComponent(sPTable, javax.swing.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cmdAdding, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(cmdAdding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sPTable, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
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
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane panel;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane sPTable;
    private swing.TrainTable table;
    // End of variables declaration//GEN-END:variables
}
