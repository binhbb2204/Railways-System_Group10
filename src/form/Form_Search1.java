package form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import scrollbar.ScrollBarCustom;
import connection.ConnectData;
import datechooser.SelectedDate;
import glasspanepopup.GlassPanePopup;
import model.Model_Error;
import swing.ScrollBar;


public class Form_Search1 extends javax.swing.JPanel {
    private HashMap<String, LocalDate> populateTimetableTable(String trainName, String departureStationName, String arrivalStationName, SelectedDate d) {
        // Convert the selected date to LocalDate
        LocalDate selectedDate = LocalDate.of(d.getYear(), d.getMonth(), d.getDay());
    
        // HashMap to store station names and their corresponding dates
        HashMap<String, LocalDate> stationDates = new HashMap<>();
        // HashMap to keep track of the last departure time for each schedule
        HashMap<String, LocalTime> lastDepartureTimePerSchedule = new HashMap<>();
    
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
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String scheduleID = rs.getString("scheduleID");
                    String departureStation = rs.getString("Departure Station");
                    String departureTimeStr = rs.getString("Departure Time");
                    LocalTime departureTime = LocalTime.parse(departureTimeStr);
    
                    // If this is not the first row and the departure time is earlier than the last departure time,
                    // it indicates a new day has started for this schedule
                    if (lastDepartureTimePerSchedule.containsKey(scheduleID) && departureTime.isBefore(lastDepartureTimePerSchedule.get(scheduleID))) {
                        selectedDate = selectedDate.plusDays(1); // Increment the date
                    }
    
                    // Update the last departure time for this schedule
                    lastDepartureTimePerSchedule.put(scheduleID, departureTime);
                    // Add or update the date for the arrival station
                    stationDates.put(arrivalStationName, selectedDate);
                    // Add or update the date for the departure station
                    stationDates.put(departureStation, selectedDate);
                    
                    
                    System.out.println(stationDates);
                }
            }
        } catch (SQLException e) {
            // Handle SQL exceptions here
            e.printStackTrace();
        }
    
       
        return stationDates;
    }

    private void populateSearchTable(String departureStationName, String arrivalStationName, SelectedDate departureDate, SelectedDate returnDate) {
        LocalDate date = LocalDate.of(departureDate.getYear(), departureDate.getMonth(), departureDate.getDay());
        LocalDate date1 = LocalDate.of(returnDate.getYear(), returnDate.getMonth(), returnDate.getDay());
        LocalDate currentDate = LocalDate.now();
        if(returnDate != null){
            date1 = LocalDate.of(returnDate.getYear(), returnDate.getMonth(), returnDate.getDay());
        }
    
        
    
        // Query for outbound journey
        String queryOutbound = "SELECT t.trainName AS 'Train Name', " +
                "(SUM(ct.capacity) - COUNT(tk.ticketID)) AS 'Available Capacity', " +
                "j_departure.departureTime AS 'Departure Time', " +
                "j_arrival.arrivalTime AS 'Arrival Time' " +
                "FROM train t " +
                "JOIN schedule sch ON t.trainID = sch.trainID " +
                "JOIN journey j_departure ON sch.scheduleID = j_departure.scheduleID " +
                "JOIN journey j_arrival ON sch.scheduleID = j_arrival.scheduleID " +
                "JOIN station s_departure ON j_departure.stationID = s_departure.stationID " +
                "JOIN station s_arrival ON j_arrival.stationID = s_arrival.stationID " +
                "JOIN coach c ON t.trainID = c.trainID " +
                "JOIN coach_type ct ON c.coach_typeID = ct.coach_typeID " +
                "LEFT JOIN ticket tk ON c.coachID = tk.coachID AND " +
                "tk.departure_stationID = s_departure.stationID AND " +
                "tk.arrival_stationID = s_arrival.stationID " +
                "WHERE s_departure.stationName = ? AND " +
                "s_arrival.stationName = ? AND " +
                "j_departure.journeyID < j_arrival.journeyID " +
                "GROUP BY t.trainName, " +
                "j_departure.departureTime, " +
                "j_arrival.arrivalTime";
    
        // Query for return journey (reversed departure and arrival stations)
        String queryReturn = "SELECT t.trainName AS 'Train Name', " +
                "(SUM(ct.capacity) - COUNT(tk.ticketID)) AS 'Available Capacity', " +
                "j_departure.departureTime AS 'Departure Time', " +
                "j_arrival.arrivalTime AS 'Arrival Time' " +
                "FROM train t " +
                "JOIN schedule sch ON t.trainID = sch.trainID " +
                "JOIN journey j_departure ON sch.scheduleID = j_departure.scheduleID " +
                "JOIN journey j_arrival ON sch.scheduleID = j_arrival.scheduleID " +
                "JOIN station s_departure ON j_departure.stationID = s_departure.stationID " +
                "JOIN station s_arrival ON j_arrival.stationID = s_arrival.stationID " +
                "JOIN coach c ON t.trainID = c.trainID " +
                "JOIN coach_type ct ON c.coach_typeID = ct.coach_typeID " +
                "LEFT JOIN ticket tk ON c.coachID = tk.coachID AND " +
                "tk.departure_stationID = s_departure.stationID AND " +
                "tk.arrival_stationID = s_arrival.stationID " +
                "WHERE s_departure.stationName = ? AND " +
                "s_arrival.stationName = ? AND " +
                "j_departure.journeyID < j_arrival.journeyID " +
                "GROUP BY t.trainName, " +
                "j_departure.departureTime, " +
                "j_arrival.arrivalTime";
    
        
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmtOutbound = conn.prepareStatement(queryOutbound);
             PreparedStatement pstmtReturn = conn.prepareStatement(queryReturn)) {
            // Set parameters for both queries
            pstmtOutbound.setString(1, departureStationName);
            pstmtOutbound.setString(2, arrivalStationName);
    
    
            pstmtReturn.setString(1, arrivalStationName);
            pstmtReturn.setString(2, departureStationName);
    
    
            try (ResultSet rsOutbound = pstmtOutbound.executeQuery(); ResultSet rsReturn = pstmtReturn.executeQuery()) {
                // Process the results for outbound journey
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                DefaultTableModel model1 = (DefaultTableModel) table1.getModel();
                model1.setRowCount(0);
                if (!rsOutbound.isBeforeFirst()) { // Check if the ResultSet is empty
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: There is no train going from " + departureStationName + " to " + arrivalStationName + " on " + date));
    
                    return;
                }
                if(date.isBefore(currentDate)){
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: The selected time of " + date + " has already elapsed. Please select a future date."));
                    
                    return;
                }
                if (date1 != null && date1.isBefore(currentDate)) {
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: The selected time of " + date1 + " has already elapsed. Please select a future date."));
                    
                    return;
                }
                while (rsOutbound.next()) {
                    // Process outbound journey results here
                    
                    String trainName = rsOutbound.getString("Train Name");
                    int availableCapacity = rsOutbound.getInt("Available Capacity");
                    String departureTime = rsOutbound.getString("Departure Time");
                    String arrivalTime = rsOutbound.getString("Arrival Time");
                    // Populate timetable and get station dates for outbound journey
                    HashMap<String, LocalDate> stationDatesOutbound = populateTimetableTable(trainName, departureStationName, arrivalStationName, departureDate);
                    // Populate timetable and get station dates for return journey
                    // Populate timetable and get station dates
                    LocalDate departureStationDate = stationDatesOutbound.get(departureStationName);
                    LocalDate arrivalStationDate = stationDatesOutbound.get(arrivalStationName);
                    DateTimeFormatter stationDateFormatter = DateTimeFormatter.ofPattern("dd-MM");
                    String formattedDepartureStationDate = departureStationDate.format(stationDateFormatter);
                    String formattedArrivalStationDate = arrivalStationDate.format(stationDateFormatter);
                    // Populate your search table with the outbound journey data
                    table.addRow(new Object[]{trainName, departureTime + " " + formattedDepartureStationDate, arrivalTime + " " + formattedArrivalStationDate, availableCapacity});
                }
    
                // Process the results for return journey
                while (rsReturn.next()) {
                    // Process return journey results here
                    // Example:
                    String trainName = rsReturn.getString("Train Name");
                    int availableCapacity = rsReturn.getInt("Available Capacity");
                    String departureTime = rsReturn.getString("Departure Time");
                    String arrivalTime = rsReturn.getString("Arrival Time");
                    // Get the dates for departure and arrival stations
                    // Populate timetable and get station dates
                    // Populate timetable and get station dates for outbound journey
                    
                    // Populate timetable and get station dates for return journey
                    HashMap<String, LocalDate> stationDatesReturn = populateTimetableTable(trainName, arrivalStationName, departureStationName, returnDate);
                    LocalDate departureStationDate = stationDatesReturn.get(departureStationName);
                    LocalDate arrivalStationDate = stationDatesReturn.get(arrivalStationName);
                    DateTimeFormatter stationDateFormatter = DateTimeFormatter.ofPattern("dd-MM");
                    String formattedDepartureStationDate = departureStationDate.format(stationDateFormatter);
                    String formattedArrivalStationDate = arrivalStationDate.format(stationDateFormatter);

                    // Populate your search table with the return journey data
                    table1.addRow(new Object[]{trainName, departureTime + " " + formattedArrivalStationDate, arrivalTime + " " + formattedDepartureStationDate, availableCapacity});
                }
            }
        }  catch (SQLException ex) {
            // Handle SQL exceptions here
            ex.printStackTrace();
        }
    }
    private void populateSeatBookedTable(){
        
    }
    
    public Form_Search1() {
        initComponents();
        txtDDirection.setVisible(false);
        txtRDirection.setVisible(false);
        spPane.setVerticalScrollBar(new ScrollBar());
        spPane.getVerticalScrollBar().setBackground(Color.WHITE);
       
        ScrollBarCustom sp = new ScrollBarCustom();
        spPane.setHorizontalScrollBar(sp);
        returnPanel.setVisible(false);
        onewayPanel.setVisible(false);
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

        Error = new component.PanelError();
        dateDeparture = new datechooser.DateChooser();
        dateReturn = new datechooser.DateChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        panelRound1 = new swing.PanelRound();
        jLabel1 = new javax.swing.JLabel();
        txtTo = new combo_suggestion.ComboBoxSuggestion();
        jLabel2 = new javax.swing.JLabel();
        txtFrom = new combo_suggestion.ComboBoxSuggestion();
        jLabel3 = new javax.swing.JLabel();
        date = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        date1 = new javax.swing.JTextField();
        rdOneWay = new radio_button.RadioButton();
        rdRoundTrip = new radio_button.RadioButton();
        searchButton = new swing.Button();
        spPane = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        onewayPanel = new swing.PanelBorder();
        spTable = new javax.swing.JScrollPane();
        table = new swing.SearchTable();
        returnPanel = new swing.PanelBorder();
        spTable1 = new javax.swing.JScrollPane();
        table1 = new swing.SearchTable();
        txtDDirection = new javax.swing.JLabel();
        txtRDirection = new javax.swing.JLabel();
        spTable3 = new javax.swing.JScrollPane();
        table3 = new swing.SearchTable();
        spTable2 = new javax.swing.JScrollPane();
        table2 = new swing.SearchTable();

        dateDeparture.setTextRefernce(date);

        dateReturn.setTextRefernce(date1);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(127, 127, 127));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("From:");

        txtTo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ha Noi", "Phu Ly", "Nam Dinh", "Ninh Binh", "Bim Son", "Thanh Hoa", "Minh Khoi", "Cho Sy", "Vinh", "Yen Trung", "Huong Pho", "Dong Le", "Dong Hoi", "Dong Ha", "Hue", "Lang Co", "Da Nang", "Tra Kieu", "Phu Cang", "Tam Ky", "Nui Thanh", "Quang Ngai", "Duc Pho", "Bong Son", "Dieu Tri", "Tuy Hoa", "Gia", "Ninh Hoa", "Gia", "Ninh Hoa", "Nha Trang", "Nga Ba", "Thap Cham", "Song Mao", "Ma Lam", "Binh Thuan", "Long Khanh", "Bien Hoa", "Sai Gon" }));

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(127, 127, 127));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("To:");

        txtFrom.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Ha Noi", "Phu Ly", "Nam Dinh", "Ninh Binh", "Bim Son", "Thanh Hoa", "Minh Khoi", "Cho Sy", "Vinh", "Yen Trung", "Huong Pho", "Dong Le", "Dong Hoi", "Dong Ha", "Hue", "Lang Co", "Da Nang", "Tra Kieu", "Phu Cang", "Tam Ky", "Nui Thanh", "Quang Ngai", "Duc Pho", "Bong Son", "Dieu Tri", "Tuy Hoa", "Gia", "Ninh Hoa", "Gia", "Ninh Hoa", "Nha Trang", "Nga Ba", "Thap Cham", "Song Mao", "Ma Lam", "Binh Thuan", "Long Khanh", "Bien Hoa", "Sai Gon" }));
        txtFrom.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFromActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(127, 127, 127));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Departure:");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(127, 127, 127));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Return:");

        buttonGroup1.add(rdOneWay);
        rdOneWay.setText("One Way");
        rdOneWay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdOneWayActionPerformed(evt);
            }
        });

        buttonGroup1.add(rdRoundTrip);
        rdRoundTrip.setText("Round Trip");
        rdRoundTrip.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdRoundTripActionPerformed(evt);
            }
        });

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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panelRound1Layout.createSequentialGroup()
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtFrom, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE))
                            .addGroup(panelRound1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtTo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(date)
                            .addComponent(date1, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE)))
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchButton, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(92, 92, 92)
                        .addComponent(rdOneWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(51, 51, 51)
                        .addComponent(rdRoundTrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(86, 86, 86))
        );
        panelRound1Layout.setVerticalGroup(
            panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelRound1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFrom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(date, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(date1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelRound1Layout.createSequentialGroup()
                        .addGroup(panelRound1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rdOneWay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rdRoundTrip, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 7, Short.MAX_VALUE))
                    .addComponent(searchButton, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE))
                .addContainerGap())
        );

        spPane.setBorder(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        onewayPanel.setBackground(new java.awt.Color(255, 255, 255));

        spTable.setBorder(null);

        table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Train Name", "Departure", "Arrival", "Available"
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

        javax.swing.GroupLayout onewayPanelLayout = new javax.swing.GroupLayout(onewayPanel);
        onewayPanel.setLayout(onewayPanelLayout);
        onewayPanelLayout.setHorizontalGroup(
            onewayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(onewayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addContainerGap())
        );
        onewayPanelLayout.setVerticalGroup(
            onewayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
        );

        returnPanel.setBackground(new java.awt.Color(255, 255, 255));

        spTable1.setBorder(null);

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Train Name", "Departure", "Arrival", "Available"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable1.setViewportView(table1);

        javax.swing.GroupLayout returnPanelLayout = new javax.swing.GroupLayout(returnPanel);
        returnPanel.setLayout(returnPanelLayout);
        returnPanelLayout.setHorizontalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, returnPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        returnPanelLayout.setVerticalGroup(
            returnPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(returnPanelLayout.createSequentialGroup()
                .addComponent(spTable1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 23, Short.MAX_VALUE))
        );

        txtDDirection.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtDDirection.setForeground(new java.awt.Color(127, 127, 127));
        txtDDirection.setText("Departure Direction");

        txtRDirection.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtRDirection.setForeground(new java.awt.Color(127, 127, 127));
        txtRDirection.setText("Return Direction");

        spTable3.setBorder(null);

        table3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Coach Type", "Coach ID", "Seat Number", "Choose"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable3.setViewportView(table3);

        spTable2.setBorder(null);

        table2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Coach Type", "Coach ID", "Seat Number", "Choose"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable2.setViewportView(table2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(onewayPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(returnPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtDDirection)
                            .addComponent(txtRDirection))
                        .addGap(263, 263, 263)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spTable3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(spTable2, javax.swing.GroupLayout.DEFAULT_SIZE, 386, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addComponent(txtDDirection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(onewayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addComponent(txtRDirection)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(returnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spTable3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(130, Short.MAX_VALUE))
        );

        spPane.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spPane, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(spPane)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtFromActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFromActionPerformed
       
    }//GEN-LAST:event_txtFromActionPerformed

    private void rdOneWayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdOneWayActionPerformed
        date1.setEnabled(false);
        date1.setDisabledTextColor(Color.GRAY);
    }//GEN-LAST:event_rdOneWayActionPerformed

    private void rdRoundTripActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdRoundTripActionPerformed
        date1.setEnabled(true);
        date1.setDisabledTextColor(Color.BLACK);
    }//GEN-LAST:event_rdRoundTripActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String departureStationName = txtFrom.getSelectedItem().toString();
        String arrivalStationName = txtTo.getSelectedItem().toString();
        SelectedDate departureDate = dateDeparture.getSelectedDate();
        SelectedDate returnDate = dateReturn.getSelectedDate();
        
    
        // Check if the round-trip radio button is selected
        // if (rdRoundTrip.isSelected()) {
        //     returnDate = dateReturn.getSelectedDate();
        //     txtDDirection.setVisible(true);
        //     txtRDirection.setVisible(true);
        // }
        
        populateSearchTable(departureStationName, arrivalStationName, departureDate, returnDate);
        
    
        // Display the table for one-way trip
        //onewayPanel.setVisible(true);
        
    
        // Display the second table for round-trip if selected
        if (rdRoundTrip.isSelected()) {
            //table1.setVisible(true);
            returnDate = dateReturn.getSelectedDate();
            returnPanel.setVisible(true);
            onewayPanel.setVisible(true);
            txtDDirection.setVisible(true);
            txtRDirection.setVisible(true);
        }
        else if(rdOneWay.isSelected()){
            onewayPanel.setVisible(true);
            returnPanel.setVisible(false);
            txtDDirection.setVisible(true);
            txtRDirection.setVisible(false);
        } 
        else {
            //table1.setVisible(false);
            returnPanel.setVisible(false);
            onewayPanel.setVisible(false);
        }
    }                                            

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.PanelError Error;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JTextField date;
    private javax.swing.JTextField date1;
    private datechooser.DateChooser dateDeparture;
    private datechooser.DateChooser dateReturn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private swing.PanelBorder onewayPanel;
    private swing.PanelRound panelRound1;
    private radio_button.RadioButton rdOneWay;
    private radio_button.RadioButton rdRoundTrip;
    private swing.PanelBorder returnPanel;
    private swing.Button searchButton;
    private javax.swing.JScrollPane spPane;
    private javax.swing.JScrollPane spTable;
    private javax.swing.JScrollPane spTable1;
    private javax.swing.JScrollPane spTable2;
    private javax.swing.JScrollPane spTable3;
    private swing.SearchTable table;
    private swing.SearchTable table1;
    private swing.SearchTable table2;
    private swing.SearchTable table3;
    private javax.swing.JLabel txtDDirection;
    private combo_suggestion.ComboBoxSuggestion txtFrom;
    private javax.swing.JLabel txtRDirection;
    private combo_suggestion.ComboBoxSuggestion txtTo;
    // End of variables declaration//GEN-END:variables
}
