package com.gavinrockwell.bevcrm;

import com.gavinrockwell.bevcrm.dao.AccountDAO;
import com.gavinrockwell.bevcrm.dao.ContactDAO;
import com.gavinrockwell.bevcrm.dao.OrderDAO;
import com.gavinrockwell.bevcrm.db.DatabaseConnection;
import com.gavinrockwell.bevcrm.ui.ConsoleUI;

import java.sql.SQLException;

/**
 * Application entry point for BevCRM.
 *
 * <p>Initialises the database connection, constructs the DAO and UI layers,
 * then launches the interactive console. The database connection is closed
 * cleanly on exit, whether the application exits normally or due to an error.
 *
 * <p>Database credentials can be supplied at runtime via system properties:
 * <pre>
 *   java -Ddb.user=myuser -Ddb.password=secret -jar bevcrm.jar
 * </pre>
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class Main {

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used; configure via system properties)
     */
    public static void main(String[] args) {
        try {
            // Establish the database connection
            DatabaseConnection db = DatabaseConnection.getInstance();

            // Construct DAOs
            AccountDAO accountDAO = new AccountDAO();
            ContactDAO contactDAO = new ContactDAO();
            OrderDAO   orderDAO   = new OrderDAO();

            // Launch the UI — runs until the user exits
            ConsoleUI ui = new ConsoleUI(accountDAO, contactDAO, orderDAO);
            ui.start();

            // Close the connection on clean exit
            db.close();

        } catch (SQLException e) {
            System.err.println("Fatal: could not connect to the database.");
            System.err.println("Make sure MySQL is running and the credentials are correct.");
            System.err.println("Details: " + e.getMessage());
            System.err.println("\nTip: supply credentials with:");
            System.err.println("  java -Ddb.user=<user> -Ddb.password=<pass> -jar bevcrm.jar");
            System.exit(1);
        }
    }
}
