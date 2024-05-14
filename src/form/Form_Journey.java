
package form;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import connection.ConnectData;
import datechooser.DateChooser;
import datechooser.SelectedDate;
import model.Model_Card;
import swing.AddingActionEvent;
import swing.ScrollBar;
import swing.TableActionCellEditor;
import swing.TableActionCellRender;
import swing.TableActionEvent;
import java.awt.*;
import java.util.List;


public class Form_Journey extends javax.swing.JPanel {
    private boolean editable = false;
    private int editableRow = -1;
    private DateChooser dateChooser;
    

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
    public void insertJourneyDataToDatabase(String journeyID, String scheduleID, String stationID, String departureDate, String arrivalTime, String departureTime){
        String query = "INSERT INTO railway_system.journey (journeyID, scheduleID, stationID, departureDate, arrivalTime, departureTime) VALUES (?, ?, ?, ?, ?, ?)";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, journeyID);
            pstmt.setString(2, scheduleID);
            pstmt.setString(3, stationID);
            pstmt.setString(4, departureDate);
            pstmt.setString(5, arrivalTime);
            pstmt.setString(6, departureTime);
            pstmt.executeUpdate();
        }
        catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving data: " + e.getMessage());
        }
    }

    public void populateJourneyTable(){
        String query = "SELECT journeyID, scheduleID, stationID, departureDate, arrivalTime, departureTime FROM railway_system.journey";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);

                List<Object[]> rows = new ArrayList<>();
                while(rs.next()){
                    String journeyID = rs.getString("journeyID");
                    String scheduleID = rs.getString("scheduleID");
                    String stationID = rs.getString("stationID");
                    String departureDate = rs.getString("departureDate");
                    String arrivalTime = rs.getString("arrivalTime");
                    String departureTime = rs.getString("departureTime");
                    rows.add(new Object[]{journeyID, scheduleID, stationID, departureDate, arrivalTime, departureTime});
                }
                
                // Sort the list of rows based on scheduleID
                Collections.sort(rows, new Comparator<Object[]>() {
                    public int compare(Object[] row1, Object[] row2) {
                        String scheduleID1 = (String) row1[1];
                        String scheduleID2 = (String) row2[1];
                        return extractNumber(scheduleID1) - extractNumber(scheduleID2);
                    }

                    private int extractNumber(String scheduleID) {
                        // Extract digits from the scheduleID and parse to integer
                        String numberStr = scheduleID.replaceAll("\\D+", "");
                        return Integer.parseInt(numberStr);
                    }
                });

                // Add sorted rows to the model
                for(Object[] row : rows){
                    model.addRow(row);
                }
        }
        catch(SQLException e){
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteJourneyDataFromDatabase(String journeyID) {
        String query = "DELETE FROM railway_system.journey WHERE journeyID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, journeyID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error deleting data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateJourneyDataInDatabase(String journeyID, String scheduleID, String stationID, String departureDate, String arrivalTime, String departureTime) {
        String query = "UPDATE railway_system.journey SET scheduleID = ?, stationID = ?, departureDate = ?, arrivalTime = ?, departureTime = ? WHERE journeyID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, scheduleID);
            pstmt.setString(2, stationID);
            pstmt.setString(3, departureDate);
            pstmt.setString(4, arrivalTime);
            pstmt.setString(5, departureTime);
            pstmt.setString(6, journeyID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean checkIfJourneyIdExists(String journeyID) {
        // Implement the logic to check if the train ID exists in the database
        // Return true if it exists, false otherwise
        // This method needs to query the database and return the result
        // Example implementation:
        String query = "SELECT COUNT(*) FROM railway_system.journey WHERE journeyID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, journeyID);
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


    public Form_Journey() {
        initComponents();

        dateChooser = new DateChooser();
        

        this.add(dateChooser);

        TableColumn departureDateColumn = table.getColumnModel().getColumn(3);
        departureDateColumn.setCellEditor(new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                if (column == 3) {
                    // Temporarily store the reference to the cell's JTextField
                    JTextField editorComponent = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                    // Set the text of the DateChooser to the current value of the cell
                    dateChooser.setTextRefernce(editorComponent);
                    dateChooser.getTextRefernce().setText(value != null ? value.toString() : "");
                    // Show the DateChooser in a dialog
                    int result = JOptionPane.showConfirmDialog(table, dateChooser, "Select Date", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    // If the user made a selection, set the text of the editor component to the selected date
                    if (result == JOptionPane.OK_OPTION) {
                        editorComponent.setText(dateChooser.getTextRefernce().getText());
                    }
                    return editorComponent;
                } else {
                    return super.getTableCellEditorComponent(table, value, isSelected, row, column);
                }
            }
        });

        // txtDate.addActionListener(new ActionListener() {
        //     @Override
        //     public void actionPerformed(ActionEvent e) {
        //         // Prompt the user to confirm their action
        //         int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to update departure dates?", "Confirmation", JOptionPane.YES_NO_OPTION);
        //         if (option == JOptionPane.YES_OPTION) {
        //             // Ask the user to select the new date
        //             String newDate = JOptionPane.showInputDialog(null, "Enter the new date (YYYY-MM-DD format):");
        //             if (newDate != null) {
        //                 // Update the departure dates in the table
        //                 for (int row = 0; row < table.getRowCount(); row++) {
        //                     // Get the departure time for the current row
        //                     String departureTime = table.getValueAt(row, 5).toString();
        //                     // Calculate the departure date based on the new date and the departure time
        //                     String departureDate = calculateDepartureDate(newDate, departureTime);
        //                     // Update the table model with the new departure date
        //                     table.setValueAt(departureDate, row, 3); // Assuming departure date is at column index 3
        //                 }
        //             }
        //         }
        //     }
        // });

        AddingActionEvent event1 = new AddingActionEvent() {
            @Override
            public void onAdding(int row) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.addRow(new Object[]{"", "", "", "", "", ""});
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
                String journeyID = model.getValueAt(row, 0).toString();

                // Delete data from the database
                deleteJourneyDataFromDatabase(journeyID);
                model.removeRow(row);
                updateTotalPassengerCountDisplay();
            }
            @Override
            public void onView(int row) {
                editableRow = row;
                editable = false;
                ((DefaultTableModel)table.getModel()).fireTableDataChanged();
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String journeyID = model.getValueAt(row, 0).toString();
                String scheduleID = model.getValueAt(row, 1).toString();
                String stationID = model.getValueAt(row, 2).toString();
                String departureDate = model.getValueAt(row, 3).toString();
                String arrivalTime = model.getValueAt(row, 4).toString();
                String departureTime = model.getValueAt(row, 5).toString();
                if (checkIfJourneyIdExists(journeyID)) {
                    // Journey ID exists, so update the record
                    updateJourneyDataInDatabase(journeyID, scheduleID, stationID, departureDate, arrivalTime, departureTime);
                } else {
                    // Journey ID does not exist, so insert a new record
                    insertJourneyDataToDatabase(journeyID, scheduleID, stationID, departureDate, arrivalTime, departureTime);
                }
                updateTotalPassengerCountDisplay();
                table.repaint();
                table.revalidate();
                
                populateJourneyTable();
            }
            
            
        };
        table.getColumnModel().getColumn(6).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(6).setCellEditor(new TableActionCellEditor(event));

        card1.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/profit.png")), "Total profit", "₫ 9,112,001,000", "increased by 5%"));
        card2.setData(new Model_Card(new ImageIcon(getClass().getResource("/icons/transport.png")), "Ticket Price", "₫ 80,000", "Price can be changed by the occasion"));
        updateTotalPassengerCountDisplay();

        //add row table
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        populateJourneyTable();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        date = new datechooser.DateChooser();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        card1 = new component.Card();
        card2 = new component.Card();
        card3 = new component.Card();
        panelBorder1 = new swing.PanelBorder();
        jLabel1 = new javax.swing.JLabel();
        spTable = new javax.swing.JScrollPane();
        table = new swing.JourneyTable();
        cmdAdding = new swing.AddingRowPanelAction();
        jLabel2 = new javax.swing.JLabel();
        txtDate = new javax.swing.JTextField();
        getDateButton = new swing.ButtonOutLine();

        date.setTextRefernce(txtDate);

        jLayeredPane1.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        card1.setColor1(new java.awt.Color(0, 102, 255));
        card1.setColor2(new java.awt.Color(102, 153, 255));
        jLayeredPane1.add(card1);

        card2.setColor1(new java.awt.Color(186, 123, 247));
        card2.setColor2(new java.awt.Color(167, 94, 236));
        jLayeredPane1.add(card2);

        card3.setColor1(new java.awt.Color(51, 153, 0));
        card3.setColor2(new java.awt.Color(102, 204, 0));
        jLayeredPane1.add(card3);

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Journey Table Design");

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Journey ID", "Schedule ID", "Station ID", "Departure Date", "Arrival Time", "Departure Time", "Action"
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

        cmdAdding.setBackground(new java.awt.Color(255, 255, 255));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("Add Row");

        getDateButton.setForeground(new java.awt.Color(127, 127, 127));
        getDateButton.setText("Get Date");
        getDateButton.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        getDateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getDateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
                    .addGroup(panelBorder1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getDateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55)
                        .addComponent(cmdAdding, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)))
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmdAdding, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txtDate)
                    .addComponent(getDateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 813, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    private void getDateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getDateButtonActionPerformed
        DateCorrection();
    }
    public void DateCorrection(){
        // Get the selected date from the date picker
        SelectedDate d = date.getSelectedDate();
        int year = d.getYear();
        int month = d.getMonth();
        int day = d.getDay();

        // Convert the selected date to LocalDate
        LocalDate selectedDate = LocalDate.of(year, month, day);

        int departureTimeColumnIndex = 5; 
        int departureDateColumnIndex = 3; 
        int scheduleIDColumnIndex = 1;

        // HashMap to keep track of the last departure time for each schedule
        HashMap<String, LocalTime> lastDepartureTimePerSchedule = new HashMap<>();
        // HashMap to keep track of the current date for each schedule
        HashMap<String, LocalDate> currentDatePerSchedule = new HashMap<>();

        // Iterate over all rows in the table
        for (int row = 0; row < table.getRowCount(); row++) {
            String scheduleID = table.getValueAt(row, scheduleIDColumnIndex).toString();
            String departureTimeStr = table.getValueAt(row, departureTimeColumnIndex).toString();
            LocalTime departureTime = LocalTime.parse(departureTimeStr);

            // Initialize the date for the schedule if not already present
            currentDatePerSchedule.putIfAbsent(scheduleID, selectedDate);

            // If this is not the first row and the departure time is earlier than the last departure time,
            // it indicates a new day has started for this schedule
            if (lastDepartureTimePerSchedule.containsKey(scheduleID) && departureTime.isBefore(lastDepartureTimePerSchedule.get(scheduleID))) {
                LocalDate newDate = currentDatePerSchedule.get(scheduleID).plusDays(1);
                currentDatePerSchedule.put(scheduleID, newDate);
            }

            // Update the last departure time for this schedule
            lastDepartureTimePerSchedule.put(scheduleID, departureTime);

            // Update the table model with the new departure date
            table.setValueAt(currentDatePerSchedule.get(scheduleID).toString(), row, departureDateColumnIndex);
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.Card card1;
    private component.Card card2;
    private component.Card card3;
    private swing.AddingRowPanelAction cmdAdding;
    private datechooser.DateChooser date;
    private swing.ButtonOutLine getDateButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLayeredPane jLayeredPane1;
    private swing.PanelBorder panelBorder1;
    private javax.swing.JScrollPane spTable;
    private swing.JourneyTable table;
    private javax.swing.JTextField txtDate;
    // End of variables declaration//GEN-END:variables
}
