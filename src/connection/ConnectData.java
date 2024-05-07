package connection;

import java.sql.*;

public class ConnectData {
    private static ConnectData instance;
    private Connection connection;
    public static final String url = "jdbc:mysql://127.224.24.1:3306/railway_system";
    public static final String url1 = "jdbc:sqlserver://TUF\\SQLEXPRESS; Database = railway_system; IntegratedSecurity = true";
    public static final String user = "root";
    public static final String pass = "12342204";
    public static final String driver_class = "com.mysql.cj.jdbc.Driver";
    public static final String driver_class_mssql = "com.microsoft.sqlserver.jdbc.SQLServerDriver";


    public static ConnectData getInstance() {
        if (instance == null) {
            instance = new ConnectData();
        }
        return instance;
    }
    
    public Connection connect(){
        try {
            Class.forName(driver_class);
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }
}
