package form;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import combo_suggestion.ComboSuggestionUI;
import scrollbar.ScrollBarCustom;
import connection.ConnectData;
import datechooser.SelectedDate;
import glasspanepopup.GlassPanePopup;
import model.Model_Error;
import panelSearchList.RemoveTableActionCellEditor;
import panelSearchList.RemoveTableActionEvent;
import panelSearchList.RemoveTableCellRender;
import panelSearchList.SearchActionCellRender;
import panelSearchList.SearchCoachType;
import panelSearchList.SearchTableActionCellEditor;
import panelSearchList.SearchTableActionEvent;
import swing.ScrollBar;

public class Form_Search1 extends javax.swing.JPanel {
    private String currentTrainName;
    private String currentTrainName1;
    private int ticketPrice;
    private int IDpassenger;
    private String oneWayDepartureTime;
    private String roundTripDepartureTime; 
    private LocalDate oneWayDepartureDate;
    private LocalDate roundTripDepartureDate;
    private String DepartureStation;
    private int seatID;
    private int seatID1;
    private String departureStationID;
    private String arrivalStationID;
    private String oneWayCoachID;
    private String roundTripCoachID;
    private String finaltrainID;
    private String finaltrainID1;
//SQL JDBC
//-----------------------------------------------------------------------------------------------------
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
        String queryOutbound = 
            "SELECT "+
            "    t.trainName AS 'Train Name', "+
            "    (SUM(ct.capacity) - COALESCE(COUNT(tk.ticketID), 0)) AS 'Available Capacity', "+
            "    j_departure.departureTime AS 'Departure Time', "+
            "    j_arrival.arrivalTime AS 'Arrival Time' "+
            "FROM "+
            "    train t "+
            "JOIN "+
            "    schedule sch ON t.trainID = sch.trainID "+
            "JOIN "+
            "    journey j_departure ON sch.scheduleID = j_departure.scheduleID "+
            "JOIN "+
            "    journey j_arrival ON sch.scheduleID = j_arrival.scheduleID "+
            "JOIN "+
            "    station s_departure ON j_departure.stationID = s_departure.stationID "+
            "JOIN "+
            "    station s_arrival ON j_arrival.stationID = s_arrival.stationID "+
            "JOIN "+
            "    coach c ON t.trainID = c.trainID "+
            "JOIN "+
            "    coach_type ct ON c.coach_typeID = ct.coach_typeID "+
            "LEFT JOIN "+
            "    (SELECT DISTINCT tk.ticketID, tk.coachID "+
            "     FROM ticket tk "+
            "     JOIN schedule s ON tk.trainID = s.trainID "+
            "     JOIN journey jd ON s.scheduleID = jd.scheduleID "+
            "     JOIN station sd ON jd.stationID = sd.stationID "+
            "     JOIN journey ja ON s.scheduleID = ja.scheduleID "+
            "     JOIN station sa ON ja.stationID = sa.stationID "+
            "     WHERE sd.stationName = ? AND sa.stationName = ?) tk "+
            "     ON c.coachID = tk.coachID "+
            "WHERE "+
            "    s_departure.stationName = ? AND "+
            "    s_arrival.stationName = ? AND "+
            "    j_departure.journeyID < j_arrival.journeyID "+
            "GROUP BY "+
            "    t.trainName, "+
            "    j_departure.departureTime, "+
            "    j_arrival.arrivalTime;";
        
    
        // Query for return journey (reversed departure and arrival stations)
        String queryReturn = "SELECT "+
        "    t.trainName AS 'Train Name', "+
        "    (SUM(ct.capacity) - COALESCE(COUNT(tk.ticketID), 0)) AS 'Available Capacity', "+
        "    j_departure.departureTime AS 'Departure Time', "+
        "    j_arrival.arrivalTime AS 'Arrival Time' "+
        "FROM "+
        "    train t "+
        "JOIN "+
        "    schedule sch ON t.trainID = sch.trainID "+
        "JOIN "+
        "    journey j_departure ON sch.scheduleID = j_departure.scheduleID "+
        "JOIN "+
        "    journey j_arrival ON sch.scheduleID = j_arrival.scheduleID "+
        "JOIN "+
        "    station s_departure ON j_departure.stationID = s_departure.stationID "+
        "JOIN "+
        "    station s_arrival ON j_arrival.stationID = s_arrival.stationID "+
        "JOIN "+
        "    coach c ON t.trainID = c.trainID "+
        "JOIN "+
        "    coach_type ct ON c.coach_typeID = ct.coach_typeID "+
        "LEFT JOIN "+
        "    (SELECT DISTINCT tk.ticketID, tk.coachID "+
        "     FROM ticket tk "+
        "     JOIN schedule s ON tk.trainID = s.trainID "+
        "     JOIN journey jd ON s.scheduleID = jd.scheduleID "+
        "     JOIN station sd ON jd.stationID = sd.stationID "+
        "     JOIN journey ja ON s.scheduleID = ja.scheduleID "+
        "     JOIN station sa ON ja.stationID = sa.stationID "+
        "     WHERE sd.stationName = ? AND sa.stationName = ?) tk "+
        "     ON c.coachID = tk.coachID "+
        "WHERE "+
        "    s_departure.stationName = ? AND "+
        "    s_arrival.stationName = ? AND "+
        "    j_departure.journeyID < j_arrival.journeyID "+
        "GROUP BY "+
        "    t.trainName, "+
        "    j_departure.departureTime, "+
        "    j_arrival.arrivalTime;";
    
        
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmtOutbound = conn.prepareStatement(queryOutbound);
             PreparedStatement pstmtReturn = conn.prepareStatement(queryReturn)) {
            // Set parameters for both queries
            pstmtOutbound.setString(1, departureStationName);
            pstmtOutbound.setString(2, arrivalStationName);
            pstmtOutbound.setString(3, departureStationName);
            pstmtOutbound.setString(4, arrivalStationName);
    
    
            pstmtReturn.setString(1, arrivalStationName);
            pstmtReturn.setString(2, departureStationName);
            pstmtReturn.setString(3, arrivalStationName);
            pstmtReturn.setString(4, departureStationName);
    
    
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
                    oneWayDepartureTime = departureTime;
                    // Populate timetable and get station dates for outbound journey
                    HashMap<String, LocalDate> stationDatesOutbound = populateTimetableTable(trainName, departureStationName, arrivalStationName, departureDate);
                    // Populate timetable and get station dates for return journey
                    // Populate timetable and get station dates
                    LocalDate departureStationDate = stationDatesOutbound.get(departureStationName);
                    LocalDate arrivalStationDate = stationDatesOutbound.get(arrivalStationName);
                    DateTimeFormatter stationDateFormatter = DateTimeFormatter.ofPattern("dd-MM");
                    String formattedDepartureStationDate = departureStationDate.format(stationDateFormatter);
                    String formattedArrivalStationDate = arrivalStationDate.format(stationDateFormatter);
                    oneWayDepartureDate = departureStationDate;
                    oneWayDepartureTime = departureTime;
                    // Populate your search table with the outbound journey data
                    populateCoachTypeTable(trainName);
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
                    roundTripDepartureTime = departureTime;
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
                    populateCoachType1Table(trainName);
                    roundTripDepartureDate = arrivalStationDate;
                    roundTripDepartureTime = departureTime;
                    //findingTrainID1(trainName);
                    // Populate your search table with the return journey data
                    table1.addRow(new Object[]{trainName, departureTime + " " + formattedArrivalStationDate, arrivalTime + " " + formattedDepartureStationDate, availableCapacity});
                }
            }
        }  catch (SQLException ex) {
            // Handle SQL exceptions here
            ex.printStackTrace();
        }
    }
    
    

    private void populateCoachTypeTable(String trainName) {
        currentTrainName = trainName;
        txtCoachType.removeAllItems();
        String query = "SELECT DISTINCT type AS 'Coach Type' " +
               "FROM railway_system.coach_type ct " +
               "JOIN railway_system.coach c ON ct.coach_typeID = c.coach_typeID " +
               "JOIN railway_system.train t ON c.trainID = t.trainID " +
               "WHERE t.trainName = ? and ct.capacity > 0 and ct.type != 'DINING_CAR'";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ) {
            pstmt.setString(1, trainName);
        
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String coachType = rs.getString("Coach Type");
                SearchCoachType type = SearchCoachType.valueOf(coachType);
                txtCoachType.addItem(type);
                SearchCoachType selectedType = (SearchCoachType) txtCoachType.getSelectedItem();
                String selectedCoachType = selectedType.name();
                populateAvailableSeatTable(trainName, selectedCoachType);
                findingTrainID(trainName);
                table2.repaint();
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    private void populateAvailableSeatTable(String trainName, String type) {
        
        String query = "SELECT " +
                       "ct.type AS coach_type, " + 
                       "c.coachID," + 
                       "s.seatNumber " +  // Added a space here
                       "FROM coach_type ct " +  // Added a space here
                       "JOIN coach c ON ct.coach_typeID = c.coach_typeID " +
                       "JOIN seat s ON c.coachID = s.coachID " +
                       "JOIN train t ON c.trainID = t.trainID " +
                       "LEFT JOIN ticket ti ON ti.coachID = c.coachID AND ti.seatID = s.seatID " +
                       "WHERE " +
                       "t.trainName = ? " +
                       "AND ct.type = ? " +
                       "AND ti.ticketID IS NULL;";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            pstmt.setString(2, type);
    
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table2.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                String coachType = rs.getString("coach_type");
                String coachID = rs.getString("c.coachID");
                
                int seatNumber = rs.getInt("s.seatNumber");
                SearchCoachType selectedType = SearchCoachType.valueOf(coachType);
                
                
                model.addRow(new Object[]{selectedType, coachID, seatNumber});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateCoachType1Table(String trainName) {
        currentTrainName1 = trainName;
        txtCoachType1.removeAllItems();
        String query = "SELECT DISTINCT type AS 'Coach Type' " +
               "FROM railway_system.coach_type ct " +
               "JOIN railway_system.coach c ON ct.coach_typeID = c.coach_typeID " +
               "JOIN railway_system.train t ON c.trainID = t.trainID " +
               "WHERE t.trainName = ? and ct.capacity > 0 and ct.type != 'DINING_CAR'";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ) {
            pstmt.setString(1, trainName);
        
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String coachType = rs.getString("Coach Type");
                SearchCoachType type = SearchCoachType.valueOf(coachType);
                txtCoachType1.addItem(type);
                SearchCoachType selectedType = (SearchCoachType) txtCoachType1.getSelectedItem();
                String selectedCoachType = selectedType.name();
                populateAvailableSeat1Table(trainName, selectedCoachType);
                findingTrainID1(trainName);
                
                //findingTrainID(trainName);
                table3.repaint();
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateAvailableSeat1Table(String trainName, String type) {
        
        String query = "SELECT " +
                       "ct.type AS coach_type, " + 
                       "c.coachID," + 
                       "s.seatNumber " +  
                       "FROM coach_type ct " +  
                       "JOIN coach c ON ct.coach_typeID = c.coach_typeID " +
                       "JOIN seat s ON c.coachID = s.coachID " +
                       "JOIN train t ON c.trainID = t.trainID " +
                       "LEFT JOIN ticket ti ON ti.coachID = c.coachID AND ti.seatID = s.seatID " +
                       "WHERE " +
                       "t.trainName = ? " +
                       "AND ct.type = ? " +
                       "AND ti.ticketID IS NULL;";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            pstmt.setString(2, type);
    
            ResultSet rs = pstmt.executeQuery();
            DefaultTableModel model = (DefaultTableModel) table3.getModel();
            model.setRowCount(0);
            while (rs.next()) {
                String coachType = rs.getString("coach_type");
                String coachID = rs.getString("c.coachID");
                
                int seatNumber = rs.getInt("s.seatNumber");
                SearchCoachType selectedType = SearchCoachType.valueOf(coachType);
                
                model.addRow(new Object[]{selectedType, coachID, seatNumber});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private int insertPassengerDatabase(String firstName, String lastName, String phoneNumber, String email) {
        String query = "SELECT passengerID FROM railway_system.passenger WHERE first_name = ? AND last_name = ? AND (phone_number = ? OR email = ?)";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, phoneNumber);
            pstmt.setString(4, email);
    
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    IDpassenger = rs.getInt("passengerID");
                    return IDpassenger; 
                } 
                else {
                    // Passenger doesn't exist; insert their info and get the new ID
                    String insertQuery = "INSERT INTO railway_system.passenger (first_name, last_name, phone_number, email, status) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                        insertStmt.setString(1, firstName);
                        insertStmt.setString(2, lastName);
                        insertStmt.setString(3, phoneNumber);
                        insertStmt.setString(4, email);
                        insertStmt.setString(5, "TICKETED");
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if something went wrong
    }
    private void populatePriceTable(String trainName, String departureStationName, String arrivalStationName) {
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
                "         j.stationID > LEAST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                               (SELECT stationID FROM station WHERE stationName = ?)) AND " +
                "         j.stationID <= GREATEST((SELECT stationID FROM station WHERE stationName = ?), " +
                "                                  (SELECT stationID FROM station WHERE stationName = ?))) " +
                "    OR " +
                "        (sch.start_stationID = (SELECT stationID FROM station WHERE stationName = 'Sai Gon') AND " +
                "         sch.end_stationID = (SELECT stationID FROM station WHERE stationName = 'Ha Noi') AND " +
                "         j.stationID < GREATEST((SELECT stationID FROM station WHERE stationName = ?), " +
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
                while (rs.next()) {
                    int price = rs.getInt("Price");
                    ticketPrice = price;
                    
                }
            }
        } catch (SQLException e) {
            GlassPanePopup.showPopup(Error);
            Error.setData(new Model_Error("Error retrieving data "));
            e.printStackTrace();
        }
    }
    private void findingTrainID(String trainName){
        String query = "select trainID from railway_system.train where trainName = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String trainId = rs.getString("trainID");
                finaltrainID = trainId;

            }
        }
        catch(SQLException e){

        }
    }
    private void findingTrainID1(String trainName){
        String query = "select trainID from railway_system.train where trainName = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                String trainId = rs.getString("trainID");
                finaltrainID1 = trainId;

            }
        }
        catch(SQLException e){

        }
    }
    private String findingStationID(String stationName) {
        String stationID = null;
        String query = "SELECT stationID FROM railway_system.station WHERE stationName = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stationName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String stationId = rs.getString("stationID");
                stationID = stationId;
            } else {
                System.err.println("Station ID not found for station name: " + stationName);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception for debugging
        }
        return stationID;
    }
    private String findingStationID1(String stationName) {
        String stationID = null;
        String query = "SELECT stationID FROM railway_system.station WHERE stationName = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, stationName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String stationId = rs.getString("stationID");
                stationID = stationId;
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log the exception for debugging
        }
        return stationID;
    }
    private void findSeatID(String coachID, int seatNumber){
        String query = "select seatID from railway_system.seat where coachID = ? and seatNumber = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, coachID);
            pstmt.setInt(2, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                seatID = rs.getInt("seatID");
            }
        } catch (SQLException e) {
            // Handle SQL exception
        }
        
    }
    private void findSeatID1(String coachID, int seatNumber){
        String query = "select seatID from railway_system.seat where coachID = ? and seatNumber = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, coachID);
            pstmt.setInt(2, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                seatID1 = rs.getInt("seatID");
            }
        } catch (SQLException e) {
            // Handle SQL exception
        }
        
    }
    private void insertTicketDatabase(){
        String query = "INSERT INTO railway_system.ticket (passengerID, trainID, coachID, seatID, departure_stationID, arrival_stationID, departureTime, departureDate, ticketPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = oneWayDepartureDate.format(formatter);
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, IDpassenger);//OK
            pstmt.setString(2, finaltrainID); //OK
            pstmt.setString(3, oneWayCoachID); //OK
            pstmt.setInt(4, seatID); //OK
            pstmt.setString(5, departureStationID); //OK
            pstmt.setString(6, arrivalStationID); //OK
            pstmt.setString(7, oneWayDepartureTime);
            pstmt.setString(8, formattedDate); 
            pstmt.setInt(9, ticketPrice); //NO
            pstmt.executeUpdate();
        }
        catch(SQLException e){
            e.printStackTrace();
        }

    }
    private void insertTicketDatabase1(){
        String query = "INSERT INTO railway_system.ticket (passengerID, trainID, coachID, seatID, departure_stationID, arrival_stationID, departureTime, departureDate, ticketPrice) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formattedDate = roundTripDepartureDate.format(formatter);
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, IDpassenger); //OK
            pstmt.setString(2, finaltrainID1); //NO
            pstmt.setString(3, roundTripCoachID); //OK
            pstmt.setInt(4, seatID1); //OK
            pstmt.setString(5, arrivalStationID);
            pstmt.setString(6, departureStationID);
            pstmt.setString(7, roundTripDepartureTime);
            pstmt.setString(8, formattedDate); 
            pstmt.setInt(9, ticketPrice);
            pstmt.executeUpdate();
        }
        catch(SQLException e){

        }

    }
    private boolean isSeatAvailable(String departureStationID, String arrivalStationID) {
        String query = "SELECT COUNT(*) AS count FROM ticket WHERE coachID = ? AND seatID = ?";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, departureStationID);
            pstmt.setString(2, arrivalStationID);
    
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private List<Object[]> fetchUpdatedData(String trainName, String type) {
        List<Object[]> dataList = new ArrayList<>();
        String query = "SELECT " +
                       "ct.type AS coach_type, " + 
                       "c.coachID, " + 
                       "s.seatNumber " +  
                       "FROM coach_type ct " +  
                       "JOIN coach c ON ct.coach_typeID = c.coach_typeID " +
                       "JOIN seat s ON c.coachID = s.coachID " +
                       "JOIN train t ON c.trainID = t.trainID " +
                       "LEFT JOIN ticket ti ON ti.coachID = c.coachID AND ti.seatID = s.seatID " +
                       "WHERE " +
                       "t.trainName = ? " +
                       "AND ct.type = ? " +
                       "AND ti.ticketID IS NULL;";
        try (Connection conn = new ConnectData().connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, trainName);
            pstmt.setString(2, type);
    
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String coachType = rs.getString("coach_type");
                String coachID = rs.getString("coachID");
                int seatNumber = rs.getInt("seatNumber");
                SearchCoachType selectedType = SearchCoachType.valueOf(coachType);
                dataList.add(new Object[]{selectedType, coachID, seatNumber});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataList;
    }
    private void refreshTableData(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0); // Clear existing data
        for (Object[] row : data) {
            model.addRow(row); // Add new data
    }
}
//-----------------------------------------------------------------------------------------------------     
    public Form_Search1() {
        
        initComponents();
        txtFirstName.setVisible(false);
        txtLastName.setVisible(false);
        txtPhoneNumber.setVisible(false);
        txtEmail.setVisible(false);
        lbTicket.setVisible(false);
        lbFirstName.setVisible(false);
        lbLastName.setVisible(false);
        lbPhoneNumber.setVisible(false);
        lbEmail.setVisible(false);
        spTable4.setVisible(false);
        buttonBook.setVisible(false);

        txtDDirection.setVisible(false);
        txtRDirection.setVisible(false);
        txtCoachType.setVisible(false);
        txtCoachType1.setVisible(false);
        spTable2.setVisible(false);
        spTable3.setVisible(false);
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
        spTable1.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        spTable2.setVerticalScrollBar(new ScrollBar());
        spTable2.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable2.getViewport().setBackground(Color.WHITE);
        spTable2.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        spTable3.setVerticalScrollBar(new ScrollBar());
        spTable3.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable3.getViewport().setBackground(Color.WHITE);
        spTable3.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);

        spTable4.setVerticalScrollBar(new ScrollBar());
        spTable4.getVerticalScrollBar().setBackground(Color.WHITE);
        spTable4.getViewport().setBackground(Color.WHITE);
        spTable4.setCorner(JScrollPane.UPPER_RIGHT_CORNER, p);
        
        
        
        // Ensure UI update
        txtCoachType.revalidate();
        txtCoachType.repaint();
        SearchTableActionEvent eventTable2 = new SearchTableActionEvent() {
            @Override
            public void onOk(int row) {
                if (row >= 0 && row < table2.getRowCount()) {
                    DefaultTableModel model2 = (DefaultTableModel) table2.getModel();
                  
                    String coachType = model2.getValueAt(row, 0).toString(); 
                    String coachID = model2.getValueAt(row, 1).toString(); 
                    int seatNumber = Integer.parseInt(model2.getValueAt(row, 2).toString()); 
                    findSeatID(coachID, seatNumber);
                    // Add the selected row data to table4
                    oneWayCoachID = coachID;
                    DefaultTableModel model4 = (DefaultTableModel) table4.getModel();
                    model4.addRow(new Object[]{currentTrainName, coachType, coachID, seatNumber});
                }
            }
        };
        SearchTableActionEvent eventTable3 = new SearchTableActionEvent() {
            @Override
            public void onOk(int row) {
                if (row >= 0 && row < table3.getRowCount()) {
                    DefaultTableModel model3 = (DefaultTableModel) table3.getModel();
                    String coachType = model3.getValueAt(row, 0).toString(); 
                    String coachID = model3.getValueAt(row, 1).toString(); 
                    int seatNumber = Integer.parseInt(model3.getValueAt(row, 2).toString()); 
                    findSeatID1(coachID, seatNumber);
                    roundTripCoachID = coachID;
                    // Add the selected row data to table4
                    DefaultTableModel model4 = (DefaultTableModel) table4.getModel();
                    model4.addRow(new Object[]{currentTrainName1, coachType, coachID, seatNumber});
                }
            }
        };
        RemoveTableActionEvent eventTable4 = new RemoveTableActionEvent() {
            @Override
            public void onRemove(int row) {
                if(table4.isEditing()){
                    table4.getCellEditor().stopCellEditing();
                }
                DefaultTableModel model = (DefaultTableModel) table4.getModel();
                model.removeRow(row);
            }
        };
        table2.getColumnModel().getColumn(3).setCellRenderer(new SearchActionCellRender());
        table2.getColumnModel().getColumn(3).setCellEditor(new SearchTableActionCellEditor(eventTable2));
        table3.getColumnModel().getColumn(3).setCellRenderer(new SearchActionCellRender());
        table3.getColumnModel().getColumn(3).setCellEditor(new SearchTableActionCellEditor(eventTable3));
        table4.getColumnModel().getColumn(4).setCellRenderer(new RemoveTableCellRender());
        table4.getColumnModel().getColumn(4).setCellEditor(new RemoveTableActionCellEditor(eventTable4));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Error = new component.PanelError();
        dateDeparture = new datechooser.DateChooser();
        dateReturn = new datechooser.DateChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        Loading = new component.PanelLoading();
        Success = new component.PanelSuccess();
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
        txtCoachType = new combo_suggestion.ComboBoxSuggestion();
        txtCoachType1 = new combo_suggestion.ComboBoxSuggestion();
        spTable4 = new javax.swing.JScrollPane();
        table4 = new swing.SearchTable();
        lbTicket = new javax.swing.JLabel();
        txtFirstName = new swing.MyTextField();
        lbFirstName = new javax.swing.JLabel();
        txtLastName = new swing.MyTextField();
        lbLastName = new javax.swing.JLabel();
        txtPhoneNumber = new swing.MyTextField();
        lbPhoneNumber = new javax.swing.JLabel();
        txtEmail = new swing.MyTextField();
        lbEmail = new javax.swing.JLabel();
        buttonBook = new swing.Button();

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
                        .addGap(100, 100, 100)
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
                .addComponent(spTable, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
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

        spTable3.setBackground(new java.awt.Color(255, 255, 255));
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

        spTable2.setBackground(new java.awt.Color(255, 255, 255));
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

        txtCoachType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCoachTypeActionPerformed(evt);
            }
        });

        txtCoachType1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCoachType1ActionPerformed(evt);
            }
        });

        spTable4.setBorder(null);

        table4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Train Name", "Coach Type", "Coach ID", "Seat Number", "Remove"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        spTable4.setViewportView(table4);

        lbTicket.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
        lbTicket.setForeground(new java.awt.Color(127, 127, 127));
        lbTicket.setText("Ticket Purchase ");

        txtFirstName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFirstNameActionPerformed(evt);
            }
        });

        lbFirstName.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbFirstName.setForeground(new java.awt.Color(127, 127, 127));
        lbFirstName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFirstName.setText("First Name:");

        lbLastName.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbLastName.setForeground(new java.awt.Color(127, 127, 127));
        lbLastName.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbLastName.setText("Last Name:");

        lbPhoneNumber.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbPhoneNumber.setForeground(new java.awt.Color(127, 127, 127));
        lbPhoneNumber.setText("Phone Number:");

        lbEmail.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lbEmail.setForeground(new java.awt.Color(127, 127, 127));
        lbEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbEmail.setText("Email:");

        buttonBook.setBackground(new java.awt.Color(0, 102, 255));
        buttonBook.setForeground(new java.awt.Color(255, 255, 255));
        buttonBook.setText("Book");
        buttonBook.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        buttonBook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBookActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lbTicket)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDDirection)
                                            .addComponent(txtRDirection)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(lbFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lbLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(lbPhoneNumber)
                                                    .addComponent(lbEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(onewayPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(returnPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(12, 12, 12)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spTable3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(spTable4, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCoachType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtCoachType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(0, 7, Short.MAX_VALUE))))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(353, 353, 353)
                        .addComponent(buttonBook, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtDDirection)
                    .addComponent(txtCoachType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(onewayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spTable2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtRDirection)
                    .addComponent(txtCoachType1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(returnPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spTable3, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lbTicket)
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbPhoneNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPhoneNumber, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(spTable4, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(buttonBook, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(456, 456, 456))
        );

        spPane.setViewportView(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(spPane)
                    .addComponent(panelRound1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(panelRound1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(spPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1054, Short.MAX_VALUE)
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

    private void txtCoachTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCoachTypeActionPerformed
        SearchCoachType selectedType = (SearchCoachType) txtCoachType.getSelectedItem();
        if (selectedType != null) {
            String selectedCoachType = selectedType.name();
        populateAvailableSeatTable(currentTrainName, selectedCoachType);  // Use the class-level variable
        }
    }//GEN-LAST:event_txtCoachTypeActionPerformed

    private void txtCoachType1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCoachType1ActionPerformed
        SearchCoachType selectedType = (SearchCoachType) txtCoachType1.getSelectedItem();
        if (selectedType != null) {
            String selectedCoachType = selectedType.name();
        populateAvailableSeat1Table(currentTrainName1, selectedCoachType);  // Use the class-level variable
        }
    }//GEN-LAST:event_txtCoachType1ActionPerformed

    private void txtFirstNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFirstNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtFirstNameActionPerformed

    private void buttonBookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBookActionPerformed
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String phoneNumber = txtPhoneNumber.getText();
        String email = txtEmail.getText();
        
        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            GlassPanePopup.showPopup(Error);
            Error.setData(new Model_Error("Error: Please fill in all required fields (First Name, Last Name, Phone Number)."));
            return;
        }
        else{
            GlassPanePopup.showPopup(Success);
            Success.setData(new Model_Error("Success: Ticket booked successfully."));
        }
        String departureStationName = ((ComboSuggestionUI)txtFrom.getUI()).getSelectedText();
        String arrivalStationName = ((ComboSuggestionUI)txtTo.getUI()).getSelectedText();
        
        insertPassengerDatabase(firstName, lastName, phoneNumber, email);
        if(rdOneWay.isSelected()){
            departureStationID = findingStationID(departureStationName);
            arrivalStationID = findingStationID1(arrivalStationName);
            if (isSeatAvailable(departureStationID, arrivalStationID)) {
                insertTicketDatabase();
            } else {
                GlassPanePopup.showPopup(Error);
                Error.setData(new Model_Error("Error: Selected seat is no longer available. Please choose another seat."));
                return;
            }
            
        }
        else if(rdRoundTrip.isSelected()){
            arrivalStationID = findingStationID1(departureStationName);
            departureStationID = findingStationID(arrivalStationName);
            if (isSeatAvailable(departureStationID, arrivalStationID)) {
                insertTicketDatabase();
                insertTicketDatabase1();
            } else {
                GlassPanePopup.showPopup(Error);
                Error.setData(new Model_Error("Error: Selected seat is no longer available. Please choose another seat."));
                return;
            }
        }
        SearchCoachType selectedType = (SearchCoachType) txtCoachType.getSelectedItem();
        String coachType = selectedType.name();
        SearchCoachType selectedType1 = (SearchCoachType) txtCoachType.getSelectedItem();
        String coachType1 = selectedType1.name();
        List<Object[]> updatedData = fetchUpdatedData(currentTrainName, coachType);
        List<Object[]> updatedData1 = fetchUpdatedData(currentTrainName1, coachType1);
        refreshTableData(table2, updatedData);
        refreshTableData(table3, updatedData1);
        table2.repaint();
        table3.repaint();
        System.out.println(IDpassenger);
        System.out.println(finaltrainID);
        System.out.println(oneWayCoachID);
        System.out.println(seatID);
        System.out.println(departureStationID);
        System.out.println(arrivalStationID);
        System.out.println(ticketPrice);
    }//GEN-LAST:event_buttonBookActionPerformed

    private void searchButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String departureStationName = ((ComboSuggestionUI)txtFrom.getUI()).getSelectedText();
        String arrivalStationName = ((ComboSuggestionUI)txtTo.getUI()).getSelectedText();
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
            spTable2.setVisible(true);
            spTable3.setVisible(true);
            txtCoachType.setVisible(true);
            txtCoachType1.setVisible(true);

            txtFirstName.setVisible(true);
            txtLastName.setVisible(true);
            txtPhoneNumber.setVisible(true);
            txtEmail.setVisible(true);
            lbTicket.setVisible(true);
            lbFirstName.setVisible(true);
            lbLastName.setVisible(true);
            lbPhoneNumber.setVisible(true);
            lbEmail.setVisible(true);
            spTable4.setVisible(true);
            buttonBook.setVisible(true);
        }
        else if(rdOneWay.isSelected()){
            onewayPanel.setVisible(true);
            returnPanel.setVisible(false);
            txtDDirection.setVisible(true);
            txtRDirection.setVisible(false);
            spTable2.setVisible(true);
            spTable3.setVisible(false);
            txtCoachType.setVisible(true);
            txtCoachType1.setVisible(false);

            txtFirstName.setVisible(true);
            txtLastName.setVisible(true);
            txtPhoneNumber.setVisible(true);
            txtEmail.setVisible(true);
            lbTicket.setVisible(true);
            lbFirstName.setVisible(true);
            lbLastName.setVisible(true);
            lbPhoneNumber.setVisible(true);
            lbEmail.setVisible(true);
            spTable4.setVisible(true);
            buttonBook.setVisible(true);
        } 
        else {
            //table1.setVisible(false);
            returnPanel.setVisible(false);
            onewayPanel.setVisible(false);
            spTable2.setVisible(false);
            spTable3.setVisible(false);
            txtCoachType.setVisible(false);
            txtCoachType1.setVisible(false);

            txtFirstName.setVisible(false);
            txtLastName.setVisible(false);
            txtPhoneNumber.setVisible(false);
            txtEmail.setVisible(false);
            lbTicket.setVisible(false);
            lbFirstName.setVisible(false);
            lbLastName.setVisible(false);
            lbPhoneNumber.setVisible(false);
            lbEmail.setVisible(false);
            spTable4.setVisible(false);
            buttonBook.setVisible(false);
        }
    }                                            

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private component.PanelError Error;
    private component.PanelLoading Loading;
    private component.PanelSuccess Success;
    private swing.Button buttonBook;
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
    private javax.swing.JLabel lbEmail;
    private javax.swing.JLabel lbFirstName;
    private javax.swing.JLabel lbLastName;
    private javax.swing.JLabel lbPhoneNumber;
    private javax.swing.JLabel lbTicket;
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
    private javax.swing.JScrollPane spTable4;
    private swing.SearchTable table;
    private swing.SearchTable table1;
    private swing.SearchTable table2;
    private swing.SearchTable table3;
    private swing.SearchTable table4;
    private combo_suggestion.ComboBoxSuggestion txtCoachType;
    private combo_suggestion.ComboBoxSuggestion txtCoachType1;
    private javax.swing.JLabel txtDDirection;
    private swing.MyTextField txtEmail;
    private swing.MyTextField txtFirstName;
    private combo_suggestion.ComboBoxSuggestion txtFrom;
    private swing.MyTextField txtLastName;
    private swing.MyTextField txtPhoneNumber;
    private javax.swing.JLabel txtRDirection;
    private combo_suggestion.ComboBoxSuggestion txtTo;
    // End of variables declaration//GEN-END:variables
}
