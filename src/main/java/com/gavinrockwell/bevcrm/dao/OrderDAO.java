package com.gavinrockwell.bevcrm.dao;

import com.gavinrockwell.bevcrm.db.DatabaseConnection;
import com.gavinrockwell.bevcrm.model.Order;
import com.gavinrockwell.bevcrm.model.OrderStatus;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) for {@link Order} persistence.
 *
 * <p>Encapsulates all SQL interactions for the {@code orders} table,
 * including status updates and account-scoped queries used in reporting.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class OrderDAO {

    // ── SQL constants ─────────────────────────────────────────────────────────

    private static final String INSERT =
            "INSERT INTO orders (account_id, product_name, quantity, unit_price, status) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SELECT_ALL =
            "SELECT id, account_id, product_name, quantity, unit_price, status, order_date " +
            "FROM orders ORDER BY order_date DESC";

    private static final String SELECT_BY_ID =
            "SELECT id, account_id, product_name, quantity, unit_price, status, order_date " +
            "FROM orders WHERE id = ?";

    private static final String SELECT_BY_ACCOUNT =
            "SELECT id, account_id, product_name, quantity, unit_price, status, order_date " +
            "FROM orders WHERE account_id = ? ORDER BY order_date DESC";

    private static final String SELECT_BY_STATUS =
            "SELECT id, account_id, product_name, quantity, unit_price, status, order_date " +
            "FROM orders WHERE status = ? ORDER BY order_date DESC";

    private static final String UPDATE =
            "UPDATE orders SET product_name=?, quantity=?, unit_price=?, status=? WHERE id=?";

    private static final String UPDATE_STATUS =
            "UPDATE orders SET status=? WHERE id=?";

    private static final String DELETE =
            "DELETE FROM orders WHERE id=?";

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Inserts a new order and returns it with its generated primary key.
     *
     * @param order the order to insert
     * @return the inserted order with its assigned id
     * @throws SQLException if the insert fails
     */
    public Order insert(Order order) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getAccountId());
            stmt.setString(2, order.getProductName());
            stmt.setInt(3, order.getQuantity());
            stmt.setBigDecimal(4, order.getUnitPrice());
            stmt.setString(5, order.getStatus().name());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return findById(keys.getInt(1))
                            .orElseThrow(() -> new SQLException("Insert succeeded but order could not be retrieved."));
                }
            }
        }
        throw new SQLException("Insert failed — no generated key returned.");
    }

    /**
     * Retrieves all orders, most recent first.
     *
     * @return list of all orders; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Order> findAll() throws SQLException {
        List<Order> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_ALL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) results.add(mapRow(rs));
        }
        return results;
    }

    /**
     * Retrieves a single order by primary key.
     *
     * @param id the order ID
     * @return an {@link Optional} containing the order, or empty if not found
     * @throws SQLException if the query fails
     */
    public Optional<Order> findById(int id) throws SQLException {
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
     * Retrieves all orders placed by a specific account.
     *
     * @param accountId the parent account ID
     * @return list of orders; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Order> findByAccountId(int accountId) throws SQLException {
        List<Order> results = new ArrayList<>();
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
     * Retrieves all orders with a specific lifecycle status.
     *
     * @param status the order status to filter by
     * @return list of matching orders; empty if none exist
     * @throws SQLException if the query fails
     */
    public List<Order> findByStatus(OrderStatus status) throws SQLException {
        List<Order> results = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(SELECT_BY_STATUS)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) results.add(mapRow(rs));
            }
        }
        return results;
    }

    /**
     * Updates all fields of an existing order.
     *
     * @param order the order to update; must have a valid id
     * @return {@code true} if a row was updated
     * @throws SQLException if the update fails
     */
    public boolean update(Order order) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE)) {
            stmt.setString(1, order.getProductName());
            stmt.setInt(2, order.getQuantity());
            stmt.setBigDecimal(3, order.getUnitPrice());
            stmt.setString(4, order.getStatus().name());
            stmt.setInt(5, order.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Convenience method to update only the status of an order — a common
     * operation in order lifecycle management.
     *
     * @param orderId   the order to update
     * @param newStatus the new status to apply
     * @return {@code true} if a row was updated
     * @throws SQLException if the update fails
     */
    public boolean updateStatus(int orderId, OrderStatus newStatus) throws SQLException {
        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement stmt = conn.prepareStatement(UPDATE_STATUS)) {
            stmt.setString(1, newStatus.name());
            stmt.setInt(2, orderId);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes an order by primary key.
     *
     * @param id the order ID to delete
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
     * Maps a result set row to an {@link Order} domain object.
     *
     * @param rs result set positioned on the row to map
     * @return hydrated Order
     * @throws SQLException if a column cannot be read
     */
    private Order mapRow(ResultSet rs) throws SQLException {
        return new Order(
                rs.getInt("id"),
                rs.getInt("account_id"),
                rs.getString("product_name"),
                rs.getInt("quantity"),
                rs.getBigDecimal("unit_price"),
                OrderStatus.valueOf(rs.getString("status")),
                rs.getTimestamp("order_date").toLocalDateTime()
        );
    }
}
