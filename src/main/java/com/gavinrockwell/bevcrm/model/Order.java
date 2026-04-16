package com.gavinrockwell.bevcrm.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Represents a product order placed by an {@link Account}.
 *
 * <p>An Order captures the product, quantity, unit price, and lifecycle
 * status. The total value is computed on demand from quantity and unit price
 * so that stored values never fall out of sync.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class Order {

    // ── Fields ────────────────────────────────────────────────────────────────

    private final int           id;
    private final int           accountId;
    private       String        productName;
    private       int           quantity;
    private       BigDecimal    unitPrice;
    private       OrderStatus   status;
    private final LocalDateTime orderDate;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs a new Order.
     *
     * @param id          database primary key; pass 0 for new records
     * @param accountId   ID of the account that placed the order
     * @param productName name of the beverage product
     * @param quantity    number of units ordered
     * @param unitPrice   price per unit
     * @param status      current lifecycle status
     * @param orderDate   timestamp the order was placed
     */
    public Order(int id, int accountId, String productName, int quantity,
                 BigDecimal unitPrice, OrderStatus status, LocalDateTime orderDate) {
        this.id          = id;
        this.accountId   = accountId;
        this.productName = productName;
        this.quantity    = quantity;
        this.unitPrice   = unitPrice;
        this.status      = status;
        this.orderDate   = orderDate;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return database primary key */
    public int getId()                  { return id; }

    /** @return ID of the account that placed this order */
    public int getAccountId()           { return accountId; }

    /** @return product name */
    public String getProductName()      { return productName; }

    /** @return quantity ordered */
    public int getQuantity()            { return quantity; }

    /** @return price per unit */
    public BigDecimal getUnitPrice()    { return unitPrice; }

    /** @return current order status */
    public OrderStatus getStatus()      { return status; }

    /** @return timestamp the order was placed */
    public LocalDateTime getOrderDate() { return orderDate; }

    /**
     * Computes the total order value as {@code quantity × unitPrice},
     * rounded to two decimal places.
     *
     * @return total order value
     */
    public BigDecimal getTotalValue() {
        return unitPrice
                .multiply(BigDecimal.valueOf(quantity))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param productName product name */
    public void setProductName(String productName)  { this.productName = productName; }

    /** @param quantity number of units */
    public void setQuantity(int quantity)           { this.quantity = quantity; }

    /** @param unitPrice price per unit */
    public void setUnitPrice(BigDecimal unitPrice)  { this.unitPrice = unitPrice; }

    /** @param status new lifecycle status */
    public void setStatus(OrderStatus status)       { this.status = status; }

    // ── Object overrides ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format(
                "Order{id=%d, product='%s', qty=%d, unitPrice=$%.2f, total=$%.2f, status=%s}",
                id, productName, quantity, unitPrice, getTotalValue(), status.label());
    }
}
