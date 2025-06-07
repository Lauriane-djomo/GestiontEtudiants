import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnexionSQLite {

    private static final String URL = "jdbc:sqlite:etudiants.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
