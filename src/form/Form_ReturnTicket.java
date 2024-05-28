
package form;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import component.PanelMessage1;
import connection.ConnectData;
import glasspanepopup.GlassPanePopup;
import model.Model_Error;
import panelSearchList.SearchActionCellRender;
import panelSearchList.SearchTableActionCellEditor;
import panelSearchList.SearchTableActionEvent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import swing.ScrollBar;


public class Form_ReturnTicket extends javax.swing.JPanel {

//SQL JDBC
//-----------------------------------------------------------------------------------------------------
    private void populateBookingInformationDatabase(String firstName, String lastName, String phoneNumber, String email){
        String query = "SELECT " +
               "    p.first_name, " +
               "    p.last_name, " +
               "    tr.trainName AS train_name, " +
               "    ti.coachID, " +
               "    s.seatNumber, " +
               "    ds.stationName AS departure_station_name, " +
               "    st.stationName AS arrival_station_name, " +
               "    j.arrivalTime, " +
               "    j.departureTime, " +
               "    ti.departureDate, " +
               "    ti.ticketPrice " +
               "FROM " +
               "    passenger p " +
               "JOIN " +
               "    ticket ti ON p.passengerID = ti.passengerID " +
               "JOIN " +
               "    train tr ON ti.trainID = tr.trainID " +
               "JOIN " +
               "    coach c ON ti.coachID = c.coachID " +
               "JOIN " +
               "    seat s ON ti.seatID = s.seatID " +
               "JOIN " +
               "    schedule sch ON ti.trainID = sch.trainID " +
               "JOIN " +
               "    station ds ON ti.departure_stationID = ds.stationID " +
               "JOIN " +
               "    station st ON ti.arrival_stationID = st.stationID " +
               "JOIN " +
               "    journey j ON sch.scheduleID = j.scheduleID AND ds.stationID = j.stationID " +
               "WHERE " +
               "    ti.departure_stationID = j.stationID " +
               "    AND p.first_name = ? " +
               "    AND p.last_name = ? " +
               "    AND (p.phone_number = ? OR p.email = ?);";
        try(Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)){
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
            
            try( ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                if (!rs.isBeforeFirst()) { 
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: No tickets found for customer "+ firstName + " " + lastName));
                    return;
                }
                while(rs.next()){
                    spTable.setVisible(true);
                    spTable.repaint();
                    String trainName = rs.getString("train_name");
                    String coachID = rs.getString("coachID");
                    int seatNumber = rs.getInt("seatNumber");
                    String departureStationName = rs.getString("departure_station_name");
                    String arrivalStationName = rs.getString("arrival_station_name");
                    String arrivalTime = rs.getString("arrivalTime");
                    String departureTime = rs.getString("departureTime");
                    String departureDate = rs.getString("departureDate");
                    int ticketPrice = rs.getInt("ticketPrice");
            
                    model.addRow(new Object[]{firstName + " " + lastName, trainName, coachID, seatNumber, departureStationName, arrivalStationName, arrivalTime, departureDate, ticketPrice});
                }
            }
            
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    private void returnTicketDatabase(String firstName, String lastName, String trainName, String coachID, int seatNumber, String departureDate) {
        String query = "delete FROM railway_system.ticket  " +
                       "WHERE passengerID IN (SELECT passengerID FROM passenger WHERE first_name = ? AND last_name = ?) " +
                       "AND trainID IN (SELECT trainID FROM train WHERE trainName = ?) " +
                       "AND coachID = ? " +
                       "AND seatID IN (SELECT seatID FROM seat WHERE seatNumber = ? AND coachID = ?) " +
                       "AND departureDate = ?";
        
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, trainName);
            pstmt.setString(4, coachID);
            pstmt.setInt(5, seatNumber);
            pstmt.setString(6, coachID); // Added to ensure the seat is in the correct coach
            pstmt.setString(7, departureDate);
        
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Ticket successfully returned.");
            } else {
                System.out.println("Error: Ticket not found.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
//-----------------------------------------------------------------------------------------------------

    public Form_ReturnTicket() {
        
        initComponents();
        
         
        spTable.setVisible(false);
        lbBooking.setVisible(false);
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        
        
        spPanel.setVerticalScrollBar(new ScrollBar());
        spPanel.getVerticalScrollBar().setBackground(Color.WHITE);
        p.setBackground(Color.WHITE);
        spPanel.getViewport().setBackground(Color.WHITE);
        spPanel.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        spTable2.setVisible(false);
        lbReturn.setVisible(false);
        spTable2.setVerticalScrollBar(new ScrollBar());
        spTable2.getVerticalScrollBar().setBackground(Color.WHITE);
        p.setBackground(Color.WHITE);
        spTable2.getViewport().setBackground(Color.WHITE);
        spTable2.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        returnButton.setVisible(false);

        SearchTableActionEvent eventTable = new SearchTableActionEvent() {
            @Override
            public void onOk(int row) {
                if(table.isEditing()){
                    table.getCellEditor().stopCellEditing();
                }

                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String name = model.getValueAt(row, 0).toString();
                String trainName = model.getValueAt(row, 1).toString();
                String coachID = model.getValueAt(row, 2).toString();
                int seatNumber = Integer.parseInt(model.getValueAt(row, 3).toString());
                String departureStationName = model.getValueAt(row, 4).toString();
                String arrivalStationName = model.getValueAt(row, 5).toString();
                String arrivalTime = model.getValueAt(row, 6).toString();
                String arrivalDate = model.getValueAt(row, 7).toString();
                int price = Integer.parseInt(model.getValueAt(row, 8).toString());
                
                model.removeRow(row);

                DefaultTableModel model2 = (DefaultTableModel) table2.getModel();
                model2.addRow(new Object[]{name, trainName, coachID, seatNumber, departureStationName, arrivalStationName, arrivalTime, arrivalDate, price});
            }
        };
        table.getColumnModel().getColumn(9).setCellRenderer(new SearchActionCellRender());
        table.getColumnModel().getColumn(9).setCellEditor(new SearchTableActionCellEditor(eventTable));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Error = new component.PanelError();
        Success = new component.PanelSuccess();
        Message = new component.PanelMessage1();
        panelRound1 = new swing.PanelRound();
        lbFirstName = new javax.swing.JLabel();
        txtFirstName = new swing.MyTextField();
        lbLastName = new javax.swing.JLabel();
        txtLastName = new swing.MyTextField();
        lbPhoneNumber = new javax.swing.JLabel();
        txtPhonenumber = new swing.MyTextField();
        searchButton = new swing.Button();
        spPanel = new javax.swing.JScrollPane();
        panelRound2 = new swing.PanelRound();
        spTable = new javax.swing.JScrollPane();
        table = new swing.BookInformationTable();
        lbBooking = new javax.swing.JLabel();
        lbReturn = new javax.swing.JLabel();
        spTable2 = new javax.swing.JScrollPane();
        table2 = new swing.BookInformationTable();
        returnButton = new swing.Button();

        lbFirstName.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lbFirstName.setForeground(new java.awt.Color(127, 127, 127));
        lbFirstName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFirstName.setText("First Name:");

        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });

        lbLastName.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lbLastName.setForeground(new java.awt.Color(127, 127, 127));
        lbLastName.setText("Last Name:");

        lbPhoneNumber.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lbPhoneNumber.setForeground(new java.awt.Color(127, 127, 127));
        lbPhoneNumber.setText("Phone Number/Email:");

        searchButton.setBackground(new java.awt.Color(0, 102, 255));
        searchButton.setForeground(new java.awt.Color(255, 255, 255));
        searchButton.setText("Search");
        searchButton.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lbPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbFirstName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbLastName)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(35, 35, 35))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addComponent(txtPhonenumber, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(356, 356, 356))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPhonenumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        spPanel.setBorder(null);

        panelRound2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Train Name", "Coach ID", "Seat Number", "From", "To", "Arrival Time", "Arrival Date", "Price", "Choose"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table.setSelectionBackground(new java.awt.Color(255, 255, 255));
        spTable.setViewportView(table);

        lbBooking.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lbBooking.setForeground(new java.awt.Color(127, 127, 127));
        lbBooking.setText("Booking Summary");

        lbReturn.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lbReturn.setForeground(new java.awt.Color(127, 127, 127));
        lbReturn.setText("Return Ticket");

        spTable2.setBorder(null);

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Train Name", "Coach ID", "Seat Number", "From", "To", "Arrival Time", "Arrival Date", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        table2.setSelectionBackground(new java.awt.Color(255, 255, 255));
        spTable2.setViewportView(table2);

        returnButton.setBackground(new java.awt.Color(0, 102, 255));
        returnButton.setForeground(new java.awt.Color(255, 255, 255));
        returnButton.setText("Return");
        returnButton.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        returnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                returnButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout panelRound2Layout = new javax.swing.GroupLayout(panelRound2);
        panelRound2.setLayout(panelRound2Layout);
        panelRound2Layout.setHorizontalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbReturn)
                    .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 796, Short.MAX_VALUE)
                    .addComponent(lbBooking)
                    .addComponent(spTable2))
                .addGap(14, 14, 14))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(returnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(350, 350, 350))
        );
        panelRound2Layout.setVerticalGroup(
            panelRound2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lbBooking)
                .addGap(20, 20, 20)
                .addComponent(spTable, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(40, 40, 40)
                .addComponent(lbReturn)
                .addGap(20, 20, 20)
                .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(returnButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(160, Short.MAX_VALUE))
        );

        spPanel.setViewportView(panelRound2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spPanel)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(spPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 669, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String phoneNumber = txtPhonenumber.getText();
        
        populateBookingInformationDatabase(firstName, lastName, phoneNumber, phoneNumber);
        if (table.getModel().getRowCount() == 0) {
            spTable.setVisible(false);
            lbBooking.setVisible(false);
            spTable2.setVisible(false);
            lbReturn.setVisible(false);
            returnButton.setVisible(false);

        } 
        else {
            spTable.setVisible(true);
            lbBooking.setVisible(true);
            spTable2.setVisible(true);
            lbReturn.setVisible(true);
            returnButton.setVisible(true);

        }
        
    }//GEN-LAST:event_searchButtonActionPerformed

    private void returnButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_returnButtonActionPerformed
        PanelMessage1 obj = new PanelMessage1();
        obj.eventBook(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlassPanePopup.closePopupLast();

                DefaultTableModel model = (DefaultTableModel) table2.getModel();
                int rowCount = table2.getRowCount();
                if(rowCount > 0){
                    if(table2.isEditing()){
                    table2.getCellEditor().stopCellEditing();
                    }
                    for(int i = rowCount - 1; i >= 0; i--){
                    String fullName = model.getValueAt(i, 0).toString();
                    String trainName = model.getValueAt(i, 1).toString();
                    String coachID = model.getValueAt(i, 2).toString();
                    int seatNumber = Integer.parseInt(model.getValueAt(i, 3).toString());
                    String departureDate = model.getValueAt(i, 7).toString();
                    String[] names = fullName.split(" ");
                    String firstName = names[0];
                    String lastName = names[1];

                    returnTicketDatabase(firstName, lastName, trainName, coachID, seatNumber, departureDate);

                    model.removeRow(i);
                }
                GlassPanePopup.showPopup(Success);
                Success.setData(new Model_Error("Your ticket has been returned."));
                }
                else{
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("No ticket selected for return."));
                }

            }
        });
        GlassPanePopup.showPopup(obj);
        
    }//GEN-LAST:event_returnButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.PanelError Error;
    private component.PanelMessage1 Message;
    private component.PanelSuccess Success;
    private javax.swing.JLabel lbBooking;
    private javax.swing.JLabel lbFirstName;
    private javax.swing.JLabel lbLastName;
    private javax.swing.JLabel lbPhoneNumber;
    private javax.swing.JLabel lbReturn;
    private swing.PanelRound panelRound1;
    private swing.PanelRound panelRound2;
    private swing.Button returnButton;
    private swing.Button searchButton;
    private javax.swing.JScrollPane spPanel;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable2;
    private swing.BookInformationTable table;
    private swing.BookInformationTable table2;
    private swing.MyTextField txtFirstName;
    private swing.MyTextField txtLastName;
    private swing.MyTextField txtPhonenumber;
    // End of variables declaration//GEN-END:variables
}
