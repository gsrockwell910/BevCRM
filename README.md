# BevCRM — Beverage Distribution CRM

A console-based Customer Relationship Management (CRM) application for the
beverage distribution industry, built in Java with JDBC and MySQL.

---

## Why I Built This

Ohanafy builds a CRM platform specifically for beverage distributors, retailers, and
suppliers — running on Salesforce with Java (Apex) at its core. This project demonstrates
that I understand both the technical stack and the business domain, and that I can build
clean, well-structured Java applications against a relational database.

---

## Features

- **Account Management** — Create, view, edit, and delete distributor, retailer, and supplier accounts
- **Contact Management** — Track individuals at each account (linked via foreign key)
- **Order Management** — Place orders, track quantities and pricing, update lifecycle status
- **Reports Dashboard** — Account type breakdown, order pipeline by status, total delivered revenue
- **Seed Data** — Ships with realistic sample data so it runs immediately

---

## Technical Highlights

| Concept | Implementation |
|---|---|
| **OOP** | Encapsulated model classes with private fields and public accessors |
| **Builder Pattern** | `Account.Builder` for readable, flexible object construction |
| **Singleton Pattern** | `DatabaseConnection` manages a single shared JDBC connection |
| **DAO Pattern** | Separate DAO class per entity — clean separation of data and business logic |
| **Enums** | `AccountType` and `OrderStatus` with display labels |
| **Javadoc** | Every class and public method is documented |
| **PreparedStatements** | All SQL uses parameterised queries — no SQL injection risk |
| **Optional** | `findById` methods return `Optional<T>` rather than null |
| **BigDecimal** | Monetary values use `BigDecimal`, not `double`, for precision |

---

## Project Structure

```
BevCRM/
├── pom.xml                          Maven build configuration
├── schema.sql                       Database schema + seed data
├── README.md
└── src/main/java/com/gavinrockwell/bevcrm/
    ├── Main.java                    Entry point
    ├── db/
    │   └── DatabaseConnection.java  Singleton JDBC connection manager
    ├── model/
    │   ├── Account.java             Account entity + Builder
    │   ├── AccountType.java         Enum: DISTRIBUTOR / RETAILER / SUPPLIER
    │   ├── Contact.java             Contact entity
    │   ├── Order.java               Order entity
    │   └── OrderStatus.java         Enum: PENDING → CONFIRMED → SHIPPED → DELIVERED
    ├── dao/
    │   ├── AccountDAO.java          CRUD for accounts
    │   ├── ContactDAO.java          CRUD for contacts
    │   └── OrderDAO.java            CRUD + status update for orders
    └── ui/
        └── ConsoleUI.java           Interactive console menu system
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

---

## Setup

**1. Create the database**

```bash
mysql -u root -p < schema.sql
```

This creates the `bevcrm` database, all tables, and loads sample data.

**2. Build the project**

```bash
mvn clean package
```

This produces `target/bevcrm-1.0.0-jar-with-dependencies.jar`.

**3. Run the application**

```bash
# With default credentials (root, no password)
java -jar target/bevcrm-1.0.0-jar-with-dependencies.jar

# With custom credentials
java -Ddb.user=myuser -Ddb.password=mypassword -jar target/bevcrm-1.0.0-jar-with-dependencies.jar
```

---

## Usage

Navigate with the numbered menu. The main menu offers:

```
1. Accounts   — List, view, add, edit, delete
2. Contacts   — List, add, edit, delete (linked to accounts)
3. Orders     — List, add, update status, delete
4. Reports    — Account summary, order pipeline, revenue totals
0. Exit
```

---

## Author

Gavin Rockwell — [github.com/gsrockwell910](https://github.com/gsrockwell910)
