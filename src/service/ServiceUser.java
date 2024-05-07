package service;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.Random;

import connection.DatabaseConnection;
import model.ModelLogin;
import model.ModelUser;

public class ServiceUser {
    private final Connection con;
    
    public ServiceUser() {
        con = DatabaseConnection.getInstance().getConnection();
    }
    
    public ModelUser login(ModelLogin login) throws SQLException{
        ModelUser data = null;
        PreparedStatement p = con.prepareStatement("select UserID, UserName, Email from railway_system.user where BINARY(Email)=? and BINARY(`Password`)=? and `Status`='Verified' limit 1", ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        p.setString(1, login.getEmail());
        p.setString(2, login.getPassword());
        ResultSet r = p.executeQuery();
        if (r.first()) {
            int userID = r.getInt(1);
            String userName = r.getString(2);
            String email = r.getString(3);
            data = new ModelUser(userID, userName, email, "");
        }
        r.close();
        p.close();
        return data;
    }
    public void insertUser(ModelUser user) throws SQLException {
        String code = generateVerifyCode();
        PreparedStatement p = con.prepareStatement("insert into railway_system.user (UserName, Email, `Password`, VerifyCode) values (?,?,?,?)", PreparedStatement.RETURN_GENERATED_KEYS);
        p.setString(1, user.getUserName());
        p.setString(2, user.getEmail());
        p.setString(3, user.getPassword());
        p.setString(4, code);
        p.execute();
        
        ResultSet r = p.getGeneratedKeys();
        if (r.next()) {
            int userID = r.getInt(1);
            user.setUserID(userID);
            user.setVerifyCode(code);
        }
        r.close();
        p.close();
    }

    private String generateVerifyCode() throws SQLException {
        DecimalFormat df = new DecimalFormat("000000");
        Random ran = new Random();
        String code;
        do {
            code = df.format(ran.nextInt(1000000));
        } while (checkDuplicateCode(code));
        return code;
    }

    private boolean checkDuplicateCode(String code) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select UserID from railway_system.user where VerifyCode=? limit 1");
        p.setString(1, code);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public boolean checkDuplicateUser(String userName) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select UserID from railway_system.user where UserName=? limit 1");
        p.setString(1, userName);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public boolean checkDuplicateEmail(String email) throws SQLException {
        boolean duplicate = false;
        PreparedStatement p = con.prepareStatement("select UserID from railway_system.user where Email=? limit 1");
        p.setString(1, email);
        ResultSet r = p.executeQuery();
        if (r.next()) {
            duplicate = true;
        }
        r.close();
        p.close();
        return duplicate;
    }

    public void doneVerify(int userID) throws SQLException {
        PreparedStatement p = con.prepareStatement("update railway_system.user set VerifyCode='', `Status`='Verified' where UserID=? limit 1");
        p.setInt(1, userID);
        p.execute();
        p.close();
    }

    public boolean verifyCodeWithUser(int userID, String code) throws SQLException {
        boolean verify = false;
        PreparedStatement p = con.prepareStatement("select UserID from railway_system.user where UserID=? and VerifyCode=? limit 1");
        p.setInt(1, userID);
        p.setString(2, code);
        ResultSet r = p.executeQuery();
        if (r.first()) {
            verify = true;
        }
        r.close();
        p.close();
        return verify;
    }
}
