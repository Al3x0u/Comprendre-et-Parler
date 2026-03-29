package be.hers.pi.comprendre_et_parler.DAOs;

import be.hers.pi.comprendre_et_parler.exceptions.ConnectionException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

import java.util.Scanner;
import java.io.Console;

public class DatabaseConnector {
    private final static String url = "jdbc:oracle:thin:@labinfo.hers.be:1521:xe";
    private static Connection connection = null;

    private DatabaseConnector() {}

    /**
     *
     * @param login the login to the database
     * @param password the password to the database
     * @post a connection to the database has been established
     * @throws SQLException if the connection failed
     */
    public static void initialize(String login, String password) throws ConnectionException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, login, password);
        }
        catch( ClassNotFoundException e) {
            throw new ConnectionException("Failed to load oracle.jdbc.driver.OracleDriver: " + e);
        }
        catch (SQLException e) {
            throw new ConnectionException("Login failed: " + e);
        }
    }

    /**
     * @post the user has been prompted through the console to provide a login and password, and a connection to the database has been established
     * @throws SQLException if the connection failed
     */
    public static void initialize() throws ConnectionException {
        Console cons = System.console();
        if (cons == null) {
            System.out.println("WARNING : Could not open a console. Your password will not be hidden. Please make sure you're not in public or run this from a terminal.");
        }
        Scanner keyb = new Scanner(System.in);
        System.out.println("Login :");
        String login = keyb.nextLine();
        System.out.println("Password :");
        String password = (cons != null) ? String.valueOf(cons.readPassword()) : keyb.nextLine();
        initialize(login, password);
    }

    /**
     *
     * @post A connection to the database has been established. If initialized() hadn't been previously called, the user has been prompted to provide a login and password
     * @return an active Connection to the database
     * @throws SQLException if the connection failed
     */
    public static Connection getInstance() throws ConnectionException {
        if (connection == null) {
            initialize();
        }
        return connection;
    }

    /**
     * @post the connection has been closed if it wasn't already
     * @throws SQLException if a database error occurs
     */
    public static void closeInstance() throws SQLException {
        if (connection != null && !connection.isClosed())
            connection.close();
    }
}
