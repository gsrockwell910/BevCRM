package com.gavinrockwell.bevcrm.model;

import java.time.LocalDateTime;

/**
 * Represents an individual contact associated with an {@link Account}.
 *
 * <p>A contact is a person at a distributor, retailer, or supplier who
 * interacts with the sales or operations team — analogous to a Salesforce
 * Contact record linked to an Account.
 *
 * @author Gavin Rockwell
 * @version 1.0.0
 */
public class Contact {

    // ── Fields ────────────────────────────────────────────────────────────────

    private final int           id;
    private final int           accountId;
    private       String        firstName;
    private       String        lastName;
    private       String        title;
    private       String        phone;
    private       String        email;
    private final LocalDateTime createdAt;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Constructs a new Contact.
     *
     * @param id         database primary key; pass 0 for new records
     * @param accountId  ID of the parent {@link Account}
     * @param firstName  given name
     * @param lastName   family name
     * @param title      job title (nullable)
     * @param phone      phone number (nullable)
     * @param email      email address (nullable)
     * @param createdAt  creation timestamp
     */
    public Contact(int id, int accountId, String firstName, String lastName,
                   String title, String phone, String email, LocalDateTime createdAt) {
        this.id        = id;
        this.accountId = accountId;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.title     = title;
        this.phone     = phone;
        this.email     = email;
        this.createdAt = createdAt;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    /** @return database primary key */
    public int getId()               { return id; }

    /** @return ID of the parent Account */
    public int getAccountId()        { return accountId; }

    /** @return given name */
    public String getFirstName()     { return firstName; }

    /** @return family name */
    public String getLastName()      { return lastName; }

    /** @return full name as "First Last" */
    public String getFullName()      { return firstName + " " + lastName; }

    /** @return job title, or {@code null} if not set */
    public String getTitle()         { return title; }

    /** @return phone number, or {@code null} if not set */
    public String getPhone()         { return phone; }

    /** @return email address, or {@code null} if not set */
    public String getEmail()         { return email; }

    /** @return creation timestamp */
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ── Setters ───────────────────────────────────────────────────────────────

    /** @param firstName given name */
    public void setFirstName(String firstName) { this.firstName = firstName; }

    /** @param lastName family name */
    public void setLastName(String lastName)   { this.lastName = lastName; }

    /** @param title job title */
    public void setTitle(String title)         { this.title = title; }

    /** @param phone phone number */
    public void setPhone(String phone)         { this.phone = phone; }

    /** @param email email address */
    public void setEmail(String email)         { this.email = email; }

    // ── Object overrides ──────────────────────────────────────────────────────

    @Override
    public String toString() {
        return String.format("Contact{id=%d, name='%s', title='%s', accountId=%d}",
                id, getFullName(), title, accountId);
    }
}
