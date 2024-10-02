package itstep.learning.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDbService implements DbService {
    private Connection connection;

    @Override
    public Connection getConnection() throws SQLException {
        if( connection == null ) {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            String connectionUrl = "jdbc:mysql://localhost:3306/javaweb?useUnicode=true&characterEncoding=utf8&useSSL=false";
            String username = "root";
            String password = "password";
            connection = DriverManager.getConnection(connectionUrl, username, password);
        }

        return connection;
    }
}
