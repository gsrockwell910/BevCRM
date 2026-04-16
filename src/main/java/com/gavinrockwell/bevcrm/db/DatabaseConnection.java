package com.gavinrockwell.bevcrm.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages the single JDBC connection to the BevCRM MySQL database.
 *
 * <p>This class implements the <em>Singleton</em> pattern so that only one
 * {@link Connection} is open at a time throughout the application lifecycle.
 * The connection is created lazily on the first call to {@link #getConnection()}
 * and reused for all subsequent calls.
 *
 * <p>Configuration is read from three system properties that can be supplied
 * on the command line at runtime, with sensible local-development defaults:
 * <pre>
 *   -Ddb.url      (default: jdbc:mysql://localhost:3306/bevcrm)
 *   -Ddb.user     (default: root)
 *   -Ddb.password (default: empty string)
 * </pre>
 *
 * <p>Example launch command:
 * <pre>
 *   java -Ddb.user=myuser -Ddb.password=secret -jar bevcrm.jar
 * </pre>
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class DatabaseConnection {

    // ── Configuration ─────────────────────────────────────────────────────────

    private static final String DEFAULT_URL  = "jdbc:mysql://localhost:3306/bevcrm?serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASS = "";

    // ── Singleton instance ────────────────────────────────────────────────────

    private static DatabaseConnection instance;
    private        Connection          connection;

    // ── Constructor (private) ─────────────────────────────────────────────────

    private DatabaseConnection() throws SQLException {
        String url  = System.getProperty("db.url",      DEFAULT_URL);
        String user = System.getProperty("db.user",     DEFAULT_USER);
        String pass = System.getProperty("db.password", DEFAULT_PASS);
        this.connection = DriverManager.getConnection(url, user, pass);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Returns the singleton instance, creating it on first call.
     *
     * @return the singleton {@code DatabaseConnection}
     * @throws SQLException if the underlying JDBC connection cannot be established
     */
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns the live JDBC {@link Connection} for use in DAO classes.
     *
     * @return active database connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the JDBC connection and resets the singleton so a fresh
     * connection can be established on the next call to {@link #getInstance()}.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Warning: could not close database connection — " + e.getMessage());
        } finally {
            instance = null;
        }
    }
}
