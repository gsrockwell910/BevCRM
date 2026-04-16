package com.gavinrockwell.bevcrm.model;

/**
 * Lifecycle stages for an {@link Order} in the BevCRM system.
 *
 * <p>Orders advance through these stages in sequence:
 * <pre>
 *   PENDING → CONFIRMED → SHIPPED → DELIVERED
 *                                 ↘ CANCELLED (from any stage)
 * </pre>
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    /**
     * Returns a capitalised, human-readable label for display in the UI.
     *
     * @return display label (e.g. "Confirmed")
     */
    public String label() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
