package hattmakaren;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Klassen DB ansvarar för att skapa anslutningar till databasen.
 *
 * Den används av DAO-klasser och andra delar av programmet som behöver
 * kommunicera med MySQL-databasen.
 */
public class DB {

    /*
     * Databasens adress.
     *
     * localhost betyder att databasen körs på samma dator.
     * hattmakaren1 är namnet på databasen som programmet ansluter till.
     *
     * Extra inställningar används för att hantera svenska tecken och tidszon.
     */
    private static final String URL =
            "jdbc:mysql://localhost:3306/hattmakaren1?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC";

    // Användarnamn till databasen.
    private static final String USER = "root";

    // Lösenord till databasen.
    private static final String PASSWORD = "byt_losenord_har";

    /**
     * Skapar och returnerar en ny databasanslutning.
     *
     * Metoden kastar SQLException om anslutningen misslyckas,
     * till exempel om databasen inte är igång eller om inloggningen är fel.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}