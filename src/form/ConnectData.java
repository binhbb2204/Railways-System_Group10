package form;

import java.sql.*;

public class ConnectData {
    public static final String url = "jdbc:mysql://192.168.1.111:3306/railway_system";
    public static final String user = "Root";
    public static final String pass = "12342204";
    public static final String driver_class = "com.mysql.cj.jdbc.Driver";

    public Connection connect(){
        try {
            Class.forName(driver_class);
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
