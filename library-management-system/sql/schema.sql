-- ============================================================================
--  LIBRARY MANAGEMENT SYSTEM - DATABASE SCHEMA
--  Software Engineering (CIC-309) | Delhi Technical Campus
--  Target: MySQL 8.0+ (also runs on MariaDB 10.5+)
-- ----------------------------------------------------------------------------
--  Run this file first, then seed.sql:
--      mysql -u root -p < sql/schema.sql
--      mysql -u root -p < sql/seed.sql
--  (Docker Compose does both automatically on the first start.)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS library_db
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE library_db;

-- Drop in reverse dependency order so the foreign keys do not complain.
DROP TABLE IF EXISTS loans;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS members;
DROP TABLE IF EXISTS app_settings;

-- ----------------------------------------------------------------------------
-- 1. BOOKS - the catalogue
--    total_copies     = how many physical copies the library owns
--    available_copies = how many of them are on the shelf right now
-- ----------------------------------------------------------------------------
CREATE TABLE books (
    book_id          VARCHAR(12)   NOT NULL,
    title            VARCHAR(150)  NOT NULL,
    author           VARCHAR(120)  NOT NULL,
    total_copies     INT           NOT NULL DEFAULT 1,
    available_copies INT           NOT NULL DEFAULT 1,
    created_at       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (book_id),
    CONSTRAINT chk_books_total     CHECK (total_copies >= 0),
    CONSTRAINT chk_books_available CHECK (available_copies >= 0),
    CONSTRAINT chk_books_balance   CHECK (available_copies <= total_copies)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 2. MEMBERS - students / faculty who borrow books
--    password_hash = BCrypt hash (60 chars), never the plain password
-- ----------------------------------------------------------------------------
CREATE TABLE members (
    member_id     VARCHAR(12)  NOT NULL,
    name          VARCHAR(120) NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    status        ENUM ('ACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (member_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 3. LOANS - one row per issue transaction
--    returned = 0 -> the book is still with the member
--    renewed  = 1 -> the single allowed renewal has been used
--    fine          -> money charged when the book came back (0 while open)
-- ----------------------------------------------------------------------------
CREATE TABLE loans (
    loan_id     INT           NOT NULL AUTO_INCREMENT,
    member_id   VARCHAR(12)   NOT NULL,
    book_id     VARCHAR(12)   NOT NULL,
    issue_date  DATE          NOT NULL,
    due_date    DATE          NOT NULL,
    return_date DATE          NULL,
    returned    BOOLEAN       NOT NULL DEFAULT FALSE,
    renewed     BOOLEAN       NOT NULL DEFAULT FALSE,
    fine        DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (loan_id),
    KEY idx_loans_member (member_id),
    KEY idx_loans_book (book_id),
    KEY idx_loans_open (returned, due_date),
    CONSTRAINT fk_loans_member FOREIGN KEY (member_id)
        REFERENCES members (member_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_loans_book FOREIGN KEY (book_id)
        REFERENCES books (book_id) ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_loans_dates CHECK (due_date >= issue_date),
    CONSTRAINT chk_loans_fine CHECK (fine >= 0)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- 4. APP_SETTINGS - key/value store for the demo feature "Simulated Today"
--    simulated_today = '' -> use the real system date
-- ----------------------------------------------------------------------------
CREATE TABLE app_settings (
    setting_key   VARCHAR(50)  NOT NULL,
    setting_value VARCHAR(255) NOT NULL DEFAULT '',
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (setting_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

-- A dedicated application user (optional when running with root).
-- Docker Compose creates this user itself from its environment variables.
-- CREATE USER IF NOT EXISTS 'lms_user'@'%' IDENTIFIED BY 'lms_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON library_db.* TO 'lms_user'@'%';
-- FLUSH PRIVILEGES;
