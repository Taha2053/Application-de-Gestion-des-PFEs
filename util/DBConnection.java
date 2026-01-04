package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL =
        "jdbc:mariadb://localhost:3306/pfe_db";
    private static final String USER = "master";
    private static final String PASSWORD = "33333";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException("Database connection failed", e);
        }
    }
}
