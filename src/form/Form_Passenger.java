
package form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import connection.ConnectData;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;
import model.Model_Card;
import model.PassengerStatus;

class PassengerManager {
    private static PassengerManager instance;
    private int totalPassengers;

    private PassengerManager() {
        // Private constructor to prevent instantiation
    }

    public static PassengerManager getInstance() {
        if (instance == null) {
            instance = new PassengerManager();
        }
        return instance;
    }

    public int getTotalPassengers() {
        return totalPassengers;
    }

    public void setTotalPassengers(int totalPassengers) {
        this.totalPassengers = totalPassengers;
    }
}


public class Form_Passenger extends javax.swing.JPanel {
    private boolean editable = false;
    private int editableRow = -1;
    private static int totalPassengers;


    // Method to update the total passenger count
    public void updateTotalPassengerCount() {
        int count = table.getModel().getRowCount();
        PassengerManager.getInstance().setTotalPassengers(count);
        // Update the display
        refreshDisplay();
    }
    public void refreshDisplay() {
        int count = PassengerManager.getInstance().getTotalPassengers();
        String formattedTotal = String.format("%,d", count);
        card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", formattedTotal, "increased by 5%"));
    }


    public static int getTotalPassengers(){
        return totalPassengers;
    }
//SQL JDBC
//-----------------------------------------------------------------------------------------------------
    private int insertPassengerDataToDatabase(String firstName, String lastName, String phoneNumber, String email, String status) {
        int generatedPassengerID = -1;
        String query = "INSERT INTO railway_system.passenger (first_name, last_name, phone_number, email, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            if (email != null && !email.isEmpty()) {
                pstmt.setString(4, email);
            } 
            else {
                pstmt.setNull(4, Types.VARCHAR);
            }
            pstmt.setString(5, status);
            pstmt.executeUpdate();
            try (ResultSet r = pstmt.getGeneratedKeys()) {
                if (r.next()) {
                    generatedPassengerID = r.getInt(1); // This is the auto-generated passengerID
                }
            }
            } 
            catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error saving data: " + e.getMessage());
            }
        return generatedPassengerID;
    }


    public void populatePassengerTable() {
        String query = "SELECT passengerID, first_name, last_name, phone_number, email, status FROM railway_system.passenger ORDER BY passengerID ASC";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
             ResultSet rs = pstmt.executeQuery()) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);
    
            while (rs.next()) {
                int passengerID = rs.getInt("passengerID");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String phoneNumber = rs.getString("phone_number");
                String email = rs.getString("email");
                String status = rs.getString("status");
                PassengerStatus passengerStatus = PassengerStatus.valueOf(status);
                if (email != null && !email.isEmpty()) {
                    
                } else {
                    email = "";
                }
                model.addRow(new Object[]{passengerID, firstName, lastName, phoneNumber, email, passengerStatus});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    
    
    private void deletePassengerDataFromDatabase(int passengerID) {
        String query = "DELETE FROM railway_system.passenger WHERE passengerID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, passengerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updatePassengerDataInDatabase(int passengerID, String firstName, String lastName, String phoneNumber, String email, String status) {
        String query = "UPDATE railway_system.passenger SET first_name = ?, last_name = ?, phone_number = ?, email = ?, status = ? WHERE passengerID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            pstmt.setString(5, status);
            pstmt.setInt(6, passengerID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean checkIfPassengerIdExists(int passengerID) {
        String query = "SELECT COUNT(*) FROM railway_system.passenger WHERE passengerID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, passengerID);
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

    public Form_Passenger() {
        initComponents();
        AddingActionEvent event1 = new AddingActionEvent() {
            @Override
            public void onAdding(int row) {
                int generatedPassengerID = insertPassengerDataToDatabase("", "", "", "", PassengerStatus.TICKETED.toString());
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{generatedPassengerID, "", "", "", "", PassengerStatus.TICKETED});
                model.fireTableDataChanged();
                updateTotalPassengerCount();
                
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
                int passengerID = Integer.parseInt(model.getValueAt(row, 0).toString());
                deletePassengerDataFromDatabase(passengerID);
                model.removeRow(row);
                updateTotalPassengerCount();
            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                int passengerID = Integer.parseInt(model.getValueAt(row, 0).toString());
                String firstName = model.getValueAt(row, 1).toString();
                String lastName = model.getValueAt(row, 2).toString();
                String phoneNumber = model.getValueAt(row, 3).toString();
                String email = model.getValueAt(row, 4).toString();
                String status = model.getValueAt(row, 5).toString();
                if (checkIfPassengerIdExists(passengerID)) {
                // Passenger ID exists, so update the record
                    updatePassengerDataInDatabase(passengerID, firstName, lastName, phoneNumber, email, status);
                } 
                else {
                // Passenger ID does not exist, so insert a new record
                    insertPassengerDataToDatabase(firstName, lastName, phoneNumber, email, status);
                }

                table.repaint();
                table.revalidate();
                populatePassengerTable();
                
            }
            
            
        };
        table.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(event));



        



        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));

        
        // card3.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/train-station.png")), "Total Passenger Count", formattedTotal, "increased by 5%"));
        
        //add row table
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        //table.getColumModel is used for the status column because it's a JComboBox
        table.getColumnModel().getColumn(5).setCellEditor(new DefaultCellEditor(new JComboBox<>(PassengerStatus.values())));
        // table.addRow(new Object[]{"01","Tran", "Thanh An", "0983127301", "thanhan@gmail.com",PassengerStatus.TICKETED});
        // table.addRow(new Object[]{"02","Nguyen", "Le Binh", "0957483948", "lebinh@gmail.com",PassengerStatus.TICKETED});
        // table.addRow(new Object[]{"03","Pham", "Van Cuong", "0956473849", "vancuong@gmail.com",PassengerStatus.CANCELLED});
        // table.addRow(new Object[]{"04","Le", "Trang Dao", "0984930293", "trangdao@gmail.com",PassengerStatus.TICKETED});
        // table.addRow(new Object[]{"05","Ly", "Minh Thong", "0949303938", "minhthong@gmail.com",PassengerStatus.ARRIVED});
        // table.addRow(new Object[]{"06","Nguyen", "Tien Dat", "0955584939", "tiendat@gmail.com",PassengerStatus.TICKETED});
        // table.addRow(new Object[]{"07","Mike", "Tyson", "0984877449", "mike@gmail.com",PassengerStatus.ARRIVED});
        // table.addRow(new Object[]{"08","Mc", "John", "0911122345", "john@gmail.com",PassengerStatus.CANCELLED});
        // table.addRow(new Object[]{"09","Tran", "Van Cao", "0940459345", "tom@gmail.com",PassengerStatus.TICKETED});
        // table.addRow(new Object[]{"10","Michael", "Jackson", "0983857491", "michael@gmail.com",PassengerStatus.TICKETED});
        populatePassengerTable();

        //this calls is used to count the total passenger, which is from that method, placing it beneath the table.addRow 
        updateTotalPassengerCount();

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
        cmdAdding = new swing.AddingRowPanelAction();
        jLabel2 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new swing.PassengerTable();

        setPreferredSize(new java.awt.Dimension(859, 480));

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
        jLabel1.setText("Passenger Table Design");

        cmdAdding.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("Add Row");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Passenger ID", "First name", "Last name", "Telephone", "Email", "Status", "Action"
            }
        ) {
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(columnIndex == 6){
                    return true;
                }
                return rowIndex == editableRow && editable;
            }
        });
        spTable.setViewportView(table);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 413, Short.MAX_VALUE)
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
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cmdAdding, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    private swing.PassengerTable table;
    // End of variables declaration//GEN-END:variables
}
