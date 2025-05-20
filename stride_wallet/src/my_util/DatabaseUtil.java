package my_util;

import java.sql.*;

public class DatabaseUtil {

    //connect to our database
    private static final String DBURL = "jdbc:mysql://localhost:3306/stridewallet_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root123456";
    
    //create our connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DBURL, USERNAME, PASSWORD);
    }
}