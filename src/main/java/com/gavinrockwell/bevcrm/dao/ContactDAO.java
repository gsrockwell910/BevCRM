package com.gavinrockwell.bevcrm.dao;

import com.gavinrockwell.bevcrm.db.DatabaseConnection;
import com.gavinrockwell.bevcrm.model.Contact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for {@link Contact} persistence.
 *
 * <p>Encapsulates all SQL interactions for the {@code contacts} table.
 * Uses {@link PreparedStatement} throughout to prevent SQL injection.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class ContactDAO {

    // ── SQL constants ─────────────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO contacts (account_id, first_name, last_name, title, phone, email) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT id, account_id, first_name, last_name, title, phone, email, created_at " +
            "FROM contacts ORDER BY last_name, first_name";

    private static final String SELECT_BY_ID =
            "SELECT id, account_id, first_name, last_name, title, phone, email, created_at " +
            "FROM contacts WHERE id = ?";

    private static final String SELECT_BY_ACCOUNT =
            "SELECT id, account_id, first_name, last_name, title, phone, email, created_at " +
            "FROM contacts WHERE account_id = ? ORDER BY last_name, first_name";

    private static final String UPDATE =
            "UPDATE contacts SET first_name=?, last_name=?, title=?, phone=?, email=? WHERE id=?";

    private static final String DELETE =
            "DELETE FROM contacts WHERE id=?";

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Inserts a new contact and returns it with its generated primary key.
     *
     * @param contact the contact to insert
     * @return the inserted contact with its assigned id
     * @throws SQLException if the insert fails
     */
    public Contact insert(Contact contact) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, contact.getAccountId());
            stmt.setString(2, contact.getFirstName());
            stmt.setString(3, contact.getLastName());
            stmt.setString(4, contact.getTitle());
            stmt.setString(5, contact.getPhone());
            stmt.setString(6, contact.getEmail());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1))
                            .orElseThrow(() -> new SQLException("Insert succeeded but contact could not be retrieved."));
                }
            }
        }
        throw new SQLException("Insert failed — no generated key returned.");
    }

    /**
     * Retrieves all contacts, ordered alphabetically by last name then first name.
     *
     * @return list of all contacts; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Contact> findAll() throws SQLException {
        List<Contact> results = new ArrayList<>();
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
     * Retrieves a single contact by primary key.
     *
     * @param id the contact ID
     * @return an {@link Optional} containing the contact, or empty if not found
     * @throws SQLException if the query fails
     */
    public Optional<Contact> findById(int id) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ID)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        }
        return Optional.empty();
    }

    /**
     * Retrieves all contacts belonging to a specific account.
     *
     * @param accountId the parent account ID
     * @return list of contacts for the account; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Contact> findByAccountId(int accountId) throws SQLException {
        List<Contact> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_ACCOUNT)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Updates an existing contact record.
     *
     * @param contact the contact to update; must have a valid id
     * @return {@code true} if a row was updated
     * @throws SQLException if the update fails
     */
    public boolean update(Contact contact) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, contact.getFirstName());
            stmt.setString(2, contact.getLastName());
            stmt.setString(3, contact.getTitle());
            stmt.setString(4, contact.getPhone());
            stmt.setString(5, contact.getEmail());
            stmt.setInt(6, contact.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a contact by primary key.
     *
     * @param id the contact ID to delete
     * @return {@code true} if a row was deleted
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
     * Maps a result set row to a {@link Contact} domain object.
     *
     * @param rs result set positioned on the row to map
     * @return hydrated Contact
     * @throws SQLException if a column cannot be read
     */
    private Contact mapRow(ResultSet rs) throws SQLException {
        return new Contact(
                rs.getInt("id"),
                rs.getInt("account_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("title"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}
