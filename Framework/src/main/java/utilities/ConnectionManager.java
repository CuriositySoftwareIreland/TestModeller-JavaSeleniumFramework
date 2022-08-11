package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private Connection con;

    public Connection getConnection(String driverName, String urlString, String username, String password) throws ClassNotFoundException, SQLException {
        Class.forName(driverName);
        con = DriverManager.getConnection(urlString, username, password);
        return con;
    }
}
