package com.gavinrockwell.bevcrm.model;

/**
 * Represents the category of a CRM account in the beverage
 * supply chain.
 *
 * <ul>
 *   <li>{@link #DISTRIBUTOR} – a wholesale distributor that purchases
 *       product from suppliers and sells to retailers.</li>
 *   <li>{@link #RETAILER} – an end-point seller such as a bar, restaurant,
 *       or grocery store.</li>
 *   <li>{@link #SUPPLIER} – a producer or manufacturer of beverage
 *       products or ingredients.</li>
 * </ul>
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public enum AccountType {
    DISTRIBUTOR,
    RETAILER,
    SUPPLIER;

    /**
     * Returns a capitalised, human-readable label for display in the UI.
     *
     * @return display label (e.g. "Distributor")
     */
    public String label() {
        String name = this.name();
        return name.charAt(0) + name.substring(1).toLowerCase();
    }
}
