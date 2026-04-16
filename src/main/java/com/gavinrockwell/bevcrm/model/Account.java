package com.gavinrockwell.bevcrm.model;

import java.time.LocalDateTime;

/**
 * Represents a business account in the BevCRM system — typically a
 * beverage distributor, retailer, or supplier.
 *
 * <p>Instances are constructed via the nested {@link Builder} to keep
 * object creation readable and to avoid telescoping constructors.
 *
 * <p>Example usage:
 * <pre>{@code
 *   Account account = new Account.Builder("Gulf Distributing Co.", AccountType.DISTRIBUTOR)
 *           .phone("251-555-0101")
 *           .email("contact@gulfdist.com")
 *           .city("Mobile")
 *           .state("AL")
 *           .build();
 * }</pre>
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class Account {

    // ── Fields ────────────────────────────────────────────────────────────────

    private final int           id;
    private       String        name;
    private       AccountType   type;
    private       String        phone;
    private       String        email;
    private       String        city;
    private       String        state;
    private final LocalDateTime createdAt;

    // ── Constructor (private — use Builder) ───────────────────────────────────

    private Account(Builder builder) {
        this.id        = builder.id;
        this.name      = builder.name;
        this.type      = builder.type;
        this.phone     = builder.phone;
        this.email     = builder.email;
        this.city      = builder.city;
        this.state     = builder.state;
        this.createdAt = builder.createdAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return database primary key; 0 if not yet persisted */
    public int getId()              { return id; }

    /** @return account name */
    public String getName()         { return name; }

    /** @return account type (DISTRIBUTOR / RETAILER / SUPPLIER) */
    public AccountType getType()    { return type; }

    /** @return primary phone number, or {@code null} if not set */
    public String getPhone()        { return phone; }

    /** @return primary email address, or {@code null} if not set */
    public String getEmail()        { return email; }

    /** @return city, or {@code null} if not set */
    public String getCity()         { return city; }

    /** @return state abbreviation, or {@code null} if not set */
    public String getState()        { return state; }

    /** @return timestamp the record was created in the database */
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param name account name (must not be null or blank) */
    public void setName(String name)       { this.name = name; }

    /** @param type account type */
    public void setType(AccountType type)  { this.type = type; }

    /** @param phone primary phone number */
    public void setPhone(String phone)     { this.phone = phone; }

    /** @param email primary email address */
    public void setEmail(String email)     { this.email = email; }

    /** @param city city */
    public void setCity(String city)       { this.city = city; }

    /** @param state state abbreviation */
    public void setState(String state)     { this.state = state; }

    // ── Object overrides ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("Account{id=%d, name='%s', type=%s, city='%s', state='%s'}",
                id, name, type.label(), city, state);
    }

    // ── Builder ───────────────────────────────────────────────────────────────

    /**
     * Fluent builder for {@link Account}.
     *
     * <p>Only {@code name} and {@code type} are required; all other fields
     * are optional and default to {@code null}.
     */
    public static class Builder {

        // Required
        private final String      name;
        private final AccountType type;

        // Optional
        private int           id        = 0;
        private String        phone     = null;
        private String        email     = null;
        private String        city      = null;
        private String        state     = null;
        private LocalDateTime createdAt = LocalDateTime.now();

        /**
         * Initialises the builder with the two required fields.
         *
         * @param name account name
         * @param type account type
         */
        public Builder(String name, AccountType type) {
            this.name = name;
            this.type = type;
        }

        /** @param id database primary key (set when hydrating from DB) */
        public Builder id(int id)                       { this.id = id;               return this; }

        /** @param phone primary phone number */
        public Builder phone(String phone)              { this.phone = phone;          return this; }

        /** @param email primary email address */
        public Builder email(String email)              { this.email = email;          return this; }

        /** @param city city */
        public Builder city(String city)                { this.city = city;            return this; }

        /** @param state state abbreviation */
        public Builder state(String state)              { this.state = state;          return this; }

        /** @param createdAt creation timestamp (set when hydrating from DB) */
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        /**
         * Constructs and returns the {@link Account} instance.
         *
         * @return new Account
         */
        public Account build() {
            return new Account(this);
        }
    }
}
