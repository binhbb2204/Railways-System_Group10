package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
public class Database {
	  private String user = "root";
	    private String pass = "";
	    private String url = "jdbc:mysql://localhost/railway management system";
	    private Statement statement;

	    public Database() throws SQLException {
	        Connection connection = DriverManager.getConnection(url, user, pass);
	        System.out.println("DB connected!!!!");
	        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	        System.out.println("connect");

	    }

	    public Statement getStatement() {
	        return statement;
	    }
}
