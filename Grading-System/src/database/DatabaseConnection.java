package database;

import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sgs";
    private static final String USER = "root";
    private static final String PASSWORD = "`ra<rNk1LG+~t*YCK3|M";

    public static Connection connect() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Connect to the database
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
