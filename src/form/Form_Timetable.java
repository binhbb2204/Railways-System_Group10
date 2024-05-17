package form;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.table.DefaultTableModel;

import combo_suggestion.ComboSuggestionUI;
import component.Message;
import component.PanelError;
import connection.ConnectData;
import datechooser.SelectedDate;
import glasspanepopup.GlassPanePopup;
import model.CoachType;
import model.Model_Error;
import model.TrainType;
import swing.ScrollBar;
import java.awt.*;


public class Form_Timetable extends javax.swing.JPanel {


//SQL JDBC
//-----------------------------------------------------------------------------------------------------
    private void populateTimetableTable(String trainName, String departureStationName, String arrivalStationName, SelectedDate d){
         // Convert the selected date to LocalDate
        LocalDate selectedDate = LocalDate.of(d.getYear(), d.getMonth(), d.getDay());
        LocalDate currentDate = LocalDate.now();
        
        // HashMap to keep track of the last departure time for each schedule
        HashMap<String, LocalTime> lastDepartureTimePerSchedule = new HashMap<>();
        // HashMap to keep track of the current date for each schedule
        HashMap<String, LocalDate> currentDatePerSchedule = new HashMap<>();

        String query = "SELECT " +
                            "j.scheduleID, " +
                            "s.stationName AS 'Departure Station', " +
                            "j.arrivalTime AS 'Arrival Time', " +
                            "j.departureTime AS 'Departure Time' " +
                          "FROM " +
                            "journey j " +
                            "JOIN schedule sch ON j.scheduleID = sch.scheduleID " +
                            "JOIN station s ON j.stationID = s.stationID " +
                            "JOIN train t ON sch.trainID = t.trainID " +
                          "WHERE " +
                            "t.trainName = ? AND " +
                            "( " +
                                "( " +
                                    "sch.start_stationID = (SELECT stationID FROM station WHERE stationName = 'Ha Noi') AND " +
                                    "sch.end_stationID = (SELECT stationID FROM station WHERE stationName = 'Sai Gon') AND " +
                                    "j.stationID >= (SELECT stationID FROM station WHERE stationName = ?) AND " +
                                    "j.stationID <= (SELECT stationID FROM station WHERE stationName = ?) " +
                                ") " +
                                "OR " +
                                "( " +
                                    "sch.start_stationID = (SELECT stationID FROM station WHERE stationName = 'Sai Gon') AND " +
                                    "sch.end_stationID = (SELECT stationID FROM station WHERE stationName = 'Ha Noi') AND " +
                                    "j.stationID <= (SELECT stationID FROM station WHERE stationName = ?) AND " +
                                    "j.stationID >= (SELECT stationID FROM station WHERE stationName = ?) " +
                                ") " +
                            ") " +
                            "AND EXISTS (SELECT 1 FROM journey WHERE stationID = (SELECT stationID FROM station WHERE stationName = ?)) " +
                            "AND EXISTS (SELECT 1 FROM journey WHERE stationID = (SELECT stationID FROM station WHERE stationName = ?));";
        try (Connection conn = new ConnectData().connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            pstmt.setString(2, departureStationName);
            pstmt.setString(3, arrivalStationName);   
            pstmt.setString(4, departureStationName);
            pstmt.setString(5, arrivalStationName);
            pstmt.setString(6, departureStationName);
            pstmt.setString(7, arrivalStationName); 
            try(ResultSet rs = pstmt.executeQuery()){
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                if (!rs.isBeforeFirst()) { // Check if the ResultSet is empty
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: There is no train going from " + departureStationName + " to " + arrivalStationName + " on " + selectedDate));

                    return;
                }
                if(selectedDate.isBefore(currentDate)){
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: The selected time of " + selectedDate + " has already elapsed. Please select a future date."));
                    
                    return;
                }
                while (rs.next()) {
                    String scheduleID = rs.getString("scheduleID"); 
                    String departureStation = rs.getString("Departure Station");
                    String arrivalTime = rs.getString("Arrival Time");
                    String departureTimeStr = rs.getString("Departure Time");
                    LocalTime departureTime = LocalTime.parse(departureTimeStr);

                
                    // Initialize the date for the schedule if not already present
                    currentDatePerSchedule.putIfAbsent(scheduleID, selectedDate);
                    
                
                    // If this is not the first row and the departure time is earlier than the last departure time,
                    // it indicates a new day has started for this schedule
                    boolean isNewDay = lastDepartureTimePerSchedule.containsKey(scheduleID) && 
                                       departureTime.isBefore(lastDepartureTimePerSchedule.get(scheduleID));
                    // Update the last departure time for this schedule
                    lastDepartureTimePerSchedule.put(scheduleID, departureTime);
                    if (isNewDay) {
                        // Increment the date for this schedule
                        LocalDate newDate = currentDatePerSchedule.get(scheduleID).plusDays(1);
                        currentDatePerSchedule.put(scheduleID, newDate);
                    }
                
                    String formattedDepartureTime = departureTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    // Add the row to the table model with the new departure date
                    model.addRow(new Object[]{departureStation, arrivalTime, formattedDepartureTime, currentDatePerSchedule.get(scheduleID).toString()});
                }  
            } 
        }
        catch (SQLException e) {
            GlassPanePopup.showPopup(Error);
            Error.setData(new Model_Error("Error retrieving data " + e.getMessage()));
            e.printStackTrace();
        }
    }

    public void populatePriceTable(String trainName, String departureStationName, String arrivalStationName, SelectedDate d) {

        LocalDate selectedDate = LocalDate.of(d.getYear(), d.getMonth(), d.getDay());
        LocalDate currentDate = LocalDate.now();
        String query =
                "SELECT DISTINCT " +
                "    ct.type AS 'Type', " +
                "    (ct.price * 1.5 * ( " +
                "        SELECT COUNT(*) " +
                "        FROM journey j " +
                "        JOIN schedule sch ON j.scheduleID = sch.scheduleID " +
                "        WHERE sch.trainID = t.trainID AND " +
                "              j.stationID BETWEEN LEAST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                                        (SELECT stationID FROM station WHERE stationName = ?)) AND " +
                "                                  GREATEST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                                            (SELECT stationID FROM station WHERE stationName = ?)) " +
                "    )) AS 'Price' " +
                "FROM " +
                "    coach c " +
                "JOIN " +
                "    train t ON c.trainID = t.trainID " +
                "JOIN " +
                "    coach_type ct ON c.coach_typeID = ct.coach_typeID " +
                "JOIN " +
                "    schedule sch ON t.trainID = sch.trainID " +
                "JOIN " +
                "    journey j ON sch.scheduleID = j.scheduleID " +
                "WHERE " +
                "    t.trainName = ? AND " +
                "    ct.price > 0 AND " +
                "    ( " +
                "        (sch.start_stationID = (SELECT stationID FROM station WHERE stationName = 'Ha Noi') AND " +
                "         sch.end_stationID = (SELECT stationID FROM station WHERE stationName = 'Sai Gon') AND " +
                "         j.stationID >= LEAST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                               (SELECT stationID FROM station WHERE stationName = ?)) AND " +
                "         j.stationID <= GREATEST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                                  (SELECT stationID FROM station WHERE stationName = ?))) " +
                "    OR " +
                "        (sch.start_stationID = (SELECT stationID FROM station WHERE stationName = 'Sai Gon') AND " +
                "         sch.end_stationID = (SELECT stationID FROM station WHERE stationName = 'Ha Noi') AND " +
                "         j.stationID <= GREATEST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                                  (SELECT stationID FROM station WHERE stationName = ?)) AND " +
                "         j.stationID >= LEAST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                               (SELECT stationID FROM station WHERE stationName = ?))) " +
                "    ) AND " +
                "    EXISTS (SELECT 1 FROM journey WHERE stationID = (SELECT stationID FROM station WHERE stationName = ?)) AND " +
                "    EXISTS (SELECT 1 FROM journey WHERE stationID = (SELECT stationID FROM station WHERE stationName = ?));";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, departureStationName);
            pstmt.setString(2, arrivalStationName);
            pstmt.setString(3, departureStationName);
            pstmt.setString(4, arrivalStationName);
            pstmt.setString(5, trainName);
            pstmt.setString(6, departureStationName);
            pstmt.setString(7, arrivalStationName);
            pstmt.setString(8, departureStationName);
            pstmt.setString(9, arrivalStationName);
            pstmt.setString(10, departureStationName);
            pstmt.setString(11, arrivalStationName);
            pstmt.setString(12, departureStationName);
            pstmt.setString(13, arrivalStationName);
            pstmt.setString(14, departureStationName);
            pstmt.setString(15, arrivalStationName);
    
    
            try (ResultSet rs = pstmt.executeQuery()) {
                DefaultTableModel model = (DefaultTableModel) table1.getModel();
                model.setRowCount(0);
                if (!rs.isBeforeFirst()) { // Check if the ResultSet is empty
                    model.setRowCount(0);
                    return;
                }
                if(selectedDate.isBefore(currentDate)){
                    model.setRowCount(0);
                    return;
                }
    
                while (rs.next()) {
                    String coachType = rs.getString("Type");
                    int price = rs.getInt("Price");
                    CoachType type = CoachType.valueOf(coachType);
                    model.addRow(new Object[]{type, price});
                }
            }
        } catch (SQLException e) {
            GlassPanePopup.showPopup(Error);
            Error.setData(new Model_Error("Error retrieving data " + e.getMessage()));
            e.printStackTrace();
        }
    }
    
    

//-----------------------------------------------------------------------------------------------------
    public Form_Timetable() {
        initComponents();
        spTable.setVerticalScrollBar(new ScrollBar());
        spTable.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable.getViewport().setBackground(Color.WHITE);
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        spTable1.setVerticalScrollBar(new ScrollBar());
        spTable1.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable1.getViewport().setBackground(Color.WHITE);
        p.setBackground(Color.WHITE);
        spTable.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        
    }


    @SuppressWarnings({ "unchecked", "rawtypes" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        date = new datechooser.DateChooser();
        Error = new component.PanelError();
        panelRound1 = new swing.PanelRound();
        txtDate = new swing.MyTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        SearchButton = new swing.Button();
        txtDeparture = new combo_suggestion.ComboBoxSuggestion();
        txtArrival = new combo_suggestion.ComboBoxSuggestion();
        txtTrain = new combo_suggestion.ComboBoxSuggestion();
        panelBorder1 = new swing.PanelBorder();
        spTable = new javax.swing.JScrollPane();
        table = new swing.TimetableTable();
        panelBorder2 = new swing.PanelBorder();
        spTable1 = new javax.swing.JScrollPane();
        table1 = new swing.PriceTable();

        date.setTextRefernce(txtDate);

        txtDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDateActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setText("Departure Date");

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setText("Departure Station");

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(127, 127, 127));
        jLabel3.setText("Arrival Station");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(127, 127, 127));
        jLabel4.setText("Selection Train");

        SearchButton.setBackground(new java.awt.Color(0, 102, 255));
        SearchButton.setForeground(new java.awt.Color(255, 255, 255));
        SearchButton.setText("Search");
        SearchButton.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        SearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SearchButtonActionPerformed(evt);
            }
        });

        txtDeparture.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ha Noi", "Phu Ly", "Nam Dinh", "Ninh Binh", "Bim Son", "Thanh Hoa", "Minh Khoi", "Cho Sy", "Vinh", "Yen Trung", "Huong Pho", "Dong Le", "Dong Hoi", "Dong Ha", "Hue", "Lang Co", "Da Nang", "Tra Kieu", "Phu Cang", "Tam Ky", "Nui Thanh", "Quang Ngai", "Duc Pho", "Bong Son", "Dieu Tri", "Tuy Hoa", "Gia", "Ninh Hoa", "Gia", "Ninh Hoa", "Nha Trang", "Nga Ba", "Thap Cham", "Song Mao", "Ma Lam", "Binh Thuan", "Long Khanh", "Bien Hoa", "Sai Gon" }));

        txtArrival.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ha Noi", "Phu Ly", "Nam Dinh", "Ninh Binh", "Bim Son", "Thanh Hoa", "Minh Khoi", "Cho Sy", "Vinh", "Yen Trung", "Huong Pho", "Dong Le", "Dong Hoi", "Dong Ha", "Hue", "Lang Co", "Da Nang", "Tra Kieu", "Phu Cang", "Tam Ky", "Nui Thanh", "Quang Ngai", "Duc Pho", "Bong Son", "Dieu Tri", "Tuy Hoa", "Gia", "Ninh Hoa", "Gia", "Ninh Hoa", "Nha Trang", "Nga Ba", "Thap Cham", "Song Mao", "Ma Lam", "Binh Thuan", "Long Khanh", "Bien Hoa", "Sai Gon" }));

        txtTrain.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SE1", "SE2", "SE3" }));

        javax.swing.GroupLayout panelRound1Layout = new javax.swing.GroupLayout(panelRound1);
        panelRound1.setLayout(panelRound1Layout);
        panelRound1Layout.setHorizontalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtDate, javax.swing.GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
                    .addComponent(txtDeparture, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(88, 88, 88)
                .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(88, 88, 88)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArrival, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTrain, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addContainerGap(19, Short.MAX_VALUE)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtArrival, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                            .addComponent(txtDeparture, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtDate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTrain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                        .addComponent(SearchButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))))
        );

        panelBorder1.setBackground(new java.awt.Color(255, 255, 255));

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Departure Station", "Arrival Time", "Departure Time", "Departure Date"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable.setViewportView(table);

        javax.swing.GroupLayout panelBorder1Layout = new javax.swing.GroupLayout(panelBorder1);
        panelBorder1.setLayout(panelBorder1Layout);
        panelBorder1Layout.setHorizontalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addContainerGap())
        );
        panelBorder1Layout.setVerticalGroup(
            panelBorder1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable)
                .addGap(20, 20, 20))
        );

        panelBorder2.setBackground(new java.awt.Color(255, 255, 255));

        spTable1.setBorder(null);

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Coach Type", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable1.setViewportView(table1);

        javax.swing.GroupLayout panelBorder2Layout = new javax.swing.GroupLayout(panelBorder2);
        panelBorder2.setLayout(panelBorder2Layout);
        panelBorder2Layout.setHorizontalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelBorder2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(109, 109, 109))
        );
        panelBorder2Layout.setVerticalGroup(
            panelBorder2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelBorder2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable1)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(panelBorder1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(panelBorder2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelBorder1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panelBorder2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDateActionPerformed

    }//GEN-LAST:event_txtDateActionPerformed

    private void SearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SearchButtonActionPerformed
        SelectedDate d = date.getSelectedDate();
        String trainName = ((ComboSuggestionUI)txtTrain.getUI()).getSelectedText();; 
        String departureStationName = ((ComboSuggestionUI)txtDeparture.getUI()).getSelectedText(); 
        String arrivalStationName = ((ComboSuggestionUI)txtArrival.getUI()).getSelectedText();

        populateTimetableTable(trainName, departureStationName, arrivalStationName, d);
        populatePriceTable(trainName, departureStationName, arrivalStationName, d);
        
    }//GEN-LAST:event_SearchButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.PanelError Error;
    private swing.Button SearchButton;
    private datechooser.DateChooser date;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private swing.PanelBorder panelBorder1;
    private swing.PanelBorder panelBorder2;
    private swing.PanelRound panelRound1;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable1;
    private swing.TimetableTable table;
    private swing.PriceTable table1;
    private combo_suggestion.ComboBoxSuggestion txtArrival;
    private swing.MyTextField txtDate;
    private combo_suggestion.ComboBoxSuggestion txtDeparture;
    private combo_suggestion.ComboBoxSuggestion txtTrain;
    // End of variables declaration//GEN-END:variables
}
