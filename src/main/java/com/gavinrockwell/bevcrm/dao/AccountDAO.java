package com.gavinrockwell.bevcrm.dao;

import com.gavinrockwell.bevcrm.db.DatabaseConnection;
import com.gavinrockwell.bevcrm.model.Account;
import com.gavinrockwell.bevcrm.model.AccountType;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for {@link Account} persistence.
 *
 * <p>This class encapsulates all SQL interactions for the {@code accounts}
 * table, exposing a clean API that the rest of the application can use
 * without knowledge of the underlying database structure.
 *
 * <p>All methods obtain their connection from the {@link DatabaseConnection}
 * singleton and use {@link PreparedStatement} to prevent SQL injection.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class AccountDAO {

    // ── SQL constants ─────────────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO accounts (name, type, phone, email, city, state) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT id, name, type, phone, email, city, state, created_at FROM accounts ORDER BY name";

    private static final String SELECT_BY_ID =
            "SELECT id, name, type, phone, email, city, state, created_at FROM accounts WHERE id = ?";

    private static final String UPDATE =
            "UPDATE accounts SET name=?, type=?, phone=?, email=?, city=?, state=? WHERE id=?";

    private static final String DELETE =
            "DELETE FROM accounts WHERE id=?";

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Persists a new account to the database and returns it with its
     * generated primary key populated.
     *
     * @param account the account to insert (id field is ignored)
     * @return the inserted account with its assigned id
     * @throws SQLException if the insert fails
     */
    public Account insert(Account account) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getType().name());
            stmt.setString(3, account.getPhone());
            stmt.setString(4, account.getEmail());
            stmt.setString(5, account.getCity());
            stmt.setString(6, account.getState());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1))
                            .orElseThrow(() -> new SQLException("Insert succeeded but account could not be retrieved."));
                }
            }
        }
        throw new SQLException("Insert failed — no generated key returned.");
    }

    /**
     * Retrieves all accounts from the database, ordered alphabetically
     * by name.
     *
     * @return list of all accounts; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Account> findAll() throws SQLException {
        List<Account> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Retrieves a single account by its primary key.
     *
     * @param id the account ID to look up
     * @return an {@link Optional} containing the account, or empty if not found
     * @throws SQLException if the query fails
     */
    public Optional<Account> findById(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Updates an existing account record with the current field values of
     * the supplied {@link Account} object.
     *
     * @param account the account to update; must have a valid id
     * @return {@code true} if a row was updated, {@code false} if no matching id exists
     * @throws SQLException if the update fails
     */
    public boolean update(Account account) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, account.getName());
            stmt.setString(2, account.getType().name());
            stmt.setString(3, account.getPhone());
            stmt.setString(4, account.getEmail());
            stmt.setString(5, account.getCity());
            stmt.setString(6, account.getState());
            stmt.setInt(7, account.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an account and all of its associated contacts and orders
     * (cascaded by the database foreign-key constraint).
     *
     * @param id the account ID to delete
     * @return {@code true} if a row was deleted, {@code false} if not found
     * @throws SQLException if the delete fails
     */
    public boolean delete(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(DELETE)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Maps a single row from the current position of a {@link ResultSet}
     * to an {@link Account} domain object.
     *
     * @param rs result set positioned on the row to map
     * @return hydrated Account
     * @throws SQLException if a column cannot be read
     */
    private Account mapRow(ResultSet rs) throws SQLException {
        return new Account.Builder(
                rs.getString("name"),
                AccountType.valueOf(rs.getString("type")))
                .id(rs.getInt("id"))
                .phone(rs.getString("phone"))
                .email(rs.getString("email"))
                .city(rs.getString("city"))
                .state(rs.getString("state"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
