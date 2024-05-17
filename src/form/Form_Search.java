package form;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JScrollBar;

import combo_suggestion.ComboSuggestionUI;
import connection.ConnectData;
import scrollbar.ScrollBarCustom;
import datechooser.SelectedDate;
import glasspanepopup.GlassPanePopup;
import model.Model_Error;
import panelSearchList.Model_Train;
import panelSearchList.trainUI;

public class Form_Search extends javax.swing.JPanel {

    private Model_Train[] getTrainDataArray(String departureStationName, String arrivalStationName, SelectedDate departureDate, SelectedDate arrivalDate) {
        LocalDate selectedDepartureDate = LocalDate.of(departureDate.getYear(), departureDate.getMonth(), departureDate.getDay());
        LocalDate selectedArrivalDate = LocalDate.of(arrivalDate.getYear(), arrivalDate.getMonth(), arrivalDate.getDay());
        LocalDate currentDate = LocalDate.now();
    
        HashMap<String, LocalTime> lastDepartureTimePerSchedule = new HashMap<>();
        HashMap<String, LocalDate> currentDatePerSchedule = new HashMap<>();
    
        String query = "SELECT t.trainName AS 'Train Name', " +
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
    
        ArrayList<Model_Train> trainDataList = new ArrayList<>();
    
        try (Connection conn = ConnectData.getInstance().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
    
            pstmt.setString(1, departureStationName);
            pstmt.setString(2, arrivalStationName);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.isBeforeFirst()) { // Check if the ResultSet is empty
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: There is no train going from " + departureStationName + " to " + arrivalStationName + " on " + selectedDepartureDate));
                    return null;
                }
                if(selectedDepartureDate.isBefore(currentDate) || selectedArrivalDate.isBefore(currentDate)){
                    GlassPanePopup.showPopup(Error);
                    Error.setData(new Model_Error("Error: The selected time of " + selectedDepartureDate + " has already elapsed. Please select a future date."));
                    return null;
                }
                while (rs.next()) {
                    String trainName = rs.getString("Train Name");
                    int availableCapacity = rs.getInt("Available Capacity");
                    Time departureTime = rs.getTime("Departure Time");
                    Time arrivalTime = rs.getTime("Arrival Time");
    
                    LocalTime departureLocalTime = departureTime.toLocalTime();
                    LocalDate departureLocalDate = selectedDepartureDate;
    
                    // If the departure time is before the last departure time, it indicates a new day has started
                    if (lastDepartureTimePerSchedule.containsKey(trainName) && departureLocalTime.isBefore(lastDepartureTimePerSchedule.get(trainName))) {
                        departureLocalDate = departureLocalDate.plusDays(1);
                        currentDatePerSchedule.put(trainName, departureLocalDate);
    
                    }
    
                    // Update the last departure time for this schedule
                    lastDepartureTimePerSchedule.put(trainName, departureLocalTime);
                    // Initialize departureLocalDate with selectedDepartureDate if it's null
                    if (departureLocalDate == null) {
                        departureLocalDate = selectedDepartureDate;
                    }
                    // Update the current date for this schedule
                    currentDatePerSchedule.put(trainName, departureLocalDate);
    
                    Model_Train trainData = new Model_Train(trainName, departureTime.toString() +" "+ lastDepartureTimePerSchedule.get(trainName).toString(), arrivalTime.toString() +" "+currentDatePerSchedule.get(trainName).toString(), availableCapacity);
                    trainDataList.add(trainData);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return trainDataList.toArray(new Model_Train[0]);
    }
    public Form_Search() {
        initComponents();
        ScrollBarCustom sp = new ScrollBarCustom();
        sp.setOrientation(JScrollBar.HORIZONTAL);
        
        scrollPanel.setVerticalScrollBar(new ScrollBarCustom());
        scrollPanel.setHorizontalScrollBar(sp);

        scroll.setVerticalScrollBar(new ScrollBarCustom());
        scroll.setHorizontalScrollBar(sp);
    }

    @SuppressWarnings("unchecked")
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
        scrollPanel = new javax.swing.JScrollPane();
        panel = new swing.PanelBorder();
        scroll = new javax.swing.JScrollPane();
        pane = new javax.swing.JLayeredPane();

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
                        .addGap(73, 73, 73)
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

        scrollPanel.setBackground(new java.awt.Color(255, 255, 255));
        scrollPanel.setBorder(null);
        scrollPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        panel.setBackground(new java.awt.Color(255, 255, 255));

        scroll.setBackground(new java.awt.Color(255, 255, 255));
        scroll.setBorder(null);
        scroll.setOpaque(false);

        pane.setBackground(new java.awt.Color(255, 255, 255));
        pane.setOpaque(true);
        pane.setLayout(new java.awt.GridLayout(1, 0, 1, 0));
        scroll.setViewportView(pane);

        javax.swing.GroupLayout panelLayout = new javax.swing.GroupLayout(panel);
        panel.setLayout(panelLayout);
        panelLayout.setHorizontalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 787, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        panelLayout.setVerticalGroup(
            panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scroll, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        scrollPanel.setViewportView(panel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scrollPanel)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(scrollPanel)
                .addGap(20, 20, 20))
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

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchButtonActionPerformed
        String departureStationName = txtFrom.getSelectedItem().toString();
        String arrivalStationName = txtTo.getSelectedItem().toString();
        SelectedDate departureDate = dateDeparture.getSelectedDate();
        SelectedDate returnDate = dateReturn.getSelectedDate();

        Model_Train[] data = getTrainDataArray(departureStationName, arrivalStationName, departureDate, returnDate);
        displayTrainData(data);
    
    }//GEN-LAST:event_searchButtonActionPerformed
    private void displayTrainData(Model_Train[] trainDataArray) {
        // Check if trainDataArray is not null before accessing its length
        if (trainDataArray != null) {
            for (Model_Train trainData : trainDataArray) {
                trainUI trainPanel = new trainUI();
                trainPanel.setData(trainData);
                pane.add(trainPanel);
                pane.setLayout(new BoxLayout(pane, BoxLayout.X_AXIS));
                pane.add(Box.createRigidArea(new Dimension(0, 10)));
            }
            pane.add(Box.createVerticalGlue());
            pane.revalidate(); // Refresh the pane to show the new components
            pane.repaint();
        } else {
            // Handle the case where trainDataArray is null
        }
    }
    
    // public Model_Train[] getTrainDataArray() {
    //     // Return an array of Model_Train objects.
    //     return new Model_Train[] {
    //         new Model_Train("Train A", "2024-05-16", "2024-05-17", "Yes"),
    //         new Model_Train("Train B", "2024-05-16", "2024-05-17", "No"),
    //         new Model_Train("Train B", "2024-05-16", "2024-05-17", "No"),
    //         new Model_Train("Train B", "2024-05-16", "2024-05-17", "No"),

            

    //         // Add more train data as needed.
    //     };
    // }

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
    private javax.swing.JLayeredPane pane;
    private swing.PanelBorder panel;
    private swing.PanelRound panelRound1;
    private radio_button.RadioButton rdOneWay;
    private radio_button.RadioButton rdRoundTrip;
    private javax.swing.JScrollPane scroll;
    private javax.swing.JScrollPane scrollPanel;
    private swing.Button searchButton;
    private combo_suggestion.ComboBoxSuggestion txtFrom;
    private combo_suggestion.ComboBoxSuggestion txtTo;
    // End of variables declaration//GEN-END:variables
}
