-- ============================================================
-- BevCRM Database Schema
-- Run this script to initialise the database before launching
-- the application.
-- ============================================================

CREATE DATABASE IF NOT EXISTS bevcrm;
USE bevcrm;

-- ------------------------------------------------------------
-- accounts
-- Represents a distributor, retailer, or supplier in the
-- beverage supply chain.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS accounts (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(150) NOT NULL,
    type        ENUM('DISTRIBUTOR', 'RETAILER', 'SUPPLIER') NOT NULL,
    phone       VARCHAR(20),
    email       VARCHAR(150),
    city        VARCHAR(100),
    state       VARCHAR(50),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

-- ------------------------------------------------------------
-- contacts
-- A person associated with an account.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS contacts (
    id          INT          NOT NULL AUTO_INCREMENT,
    account_id  INT          NOT NULL,
    first_name  VARCHAR(80)  NOT NULL,
    last_name   VARCHAR(80)  NOT NULL,
    title       VARCHAR(100),
    phone       VARCHAR(20),
    email       VARCHAR(150),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_contact_account
        FOREIGN KEY (account_id) REFERENCES accounts (id)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- orders
-- A product order placed by an account.
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id              INT             NOT NULL AUTO_INCREMENT,
    account_id      INT             NOT NULL,
    product_name    VARCHAR(150)    NOT NULL,
    quantity        INT             NOT NULL,
    unit_price      DECIMAL(10, 2)  NOT NULL,
    status          ENUM('PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED', 'CANCELLED')
                                    NOT NULL DEFAULT 'PENDING',
    order_date      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_order_account
        FOREIGN KEY (account_id) REFERENCES accounts (id)
        ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- Sample seed data
-- ------------------------------------------------------------
INSERT INTO accounts (name, type, phone, email, city, state) VALUES
    ('Gulf Distributing Co.',       'DISTRIBUTOR', '251-555-0101', 'contact@gulfdist.com',    'Mobile',      'AL'),
    ('Nashville Beverage Group',    'DISTRIBUTOR', '615-555-0182', 'info@nashbev.com',        'Nashville',   'TN'),
    ('River City Liquors',          'RETAILER',    '901-555-0233', 'orders@rivercity.com',    'Memphis',     'TN'),
    ('Blue Ridge Brewing Supply',   'SUPPLIER',    '828-555-0317', 'sales@blueridgebrew.com', 'Asheville',   'NC');

INSERT INTO contacts (account_id, first_name, last_name, title, phone, email) VALUES
    (1, 'Maria',  'Santos',  'VP of Operations',  '251-555-0102', 'msantos@gulfdist.com'),
    (1, 'Derek',  'Holt',    'Sales Manager',     '251-555-0103', 'dholt@gulfdist.com'),
    (2, 'Angela', 'Park',    'Account Manager',   '615-555-0183', 'apark@nashbev.com'),
    (3, 'Tom',    'Briggs',  'Purchasing Lead',   '901-555-0234', 'tbriggs@rivercity.com'),
    (4, 'Cassie', 'Nguyen',  'Sales Director',    '828-555-0318', 'cnguyen@blueridgebrew.com');

INSERT INTO orders (account_id, product_name, quantity, unit_price, status) VALUES
    (1, 'Coors Light 24-pack',      200, 18.50, 'DELIVERED'),
    (1, 'Modelo Especial Keg',       12, 95.00, 'SHIPPED'),
    (2, 'White Claw Variety 12-pk', 150, 14.99, 'CONFIRMED'),
    (3, 'Jack Daniels 750ml',        80, 22.00, 'PENDING'),
    (4, 'Brewing Hops — Cascade',   500,  3.75, 'DELIVERED');
