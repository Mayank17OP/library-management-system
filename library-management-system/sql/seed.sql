-- ============================================================================
--  LIBRARY MANAGEMENT SYSTEM - DEMO DATA
--  Run AFTER schema.sql:     mysql -u root -p < sql/seed.sql
-- ----------------------------------------------------------------------------
--  All dates are relative to CURDATE(), so the demo is always "fresh":
--    * loan 1 is on time,
--    * loan 2 is 5 days overdue      -> fine Rs 10,
--    * loan 3 is 101 days overdue    -> member M004 is SUSPENDED,
--    * loans 4 and 5 are already returned (history, one with a Rs 6 fine).
--
--  Passwords: every member has the demo password  member123
--  (stored as a BCrypt hash, cost 10 - never in plain text)
-- ============================================================================

USE library_db;

-- Clean start, so the script can be re-run safely.
DELETE FROM loans;
DELETE FROM books;
DELETE FROM members;
DELETE FROM app_settings;

-- ----------------------------------------------------------------------------
-- 1. BOOKS (12 titles)
--    available_copies already accounts for the open loans inserted below.
-- ----------------------------------------------------------------------------
INSERT INTO books (book_id, title, author, total_copies, available_copies) VALUES
    ('B001', 'Let Us Java', 'Yashavant Kanetkar', 5, 5),
    ('B002', 'Clean Code', 'Robert C. Martin', 3, 3),
    ('B003', 'Introduction to Algorithms', 'Cormen, Leiserson, Rivest and Stein', 4, 3),
    ('B004', 'Database System Concepts', 'Abraham Silberschatz', 3, 3),
    ('B005', 'Operating System Concepts', 'Peter B. Galvin', 3, 3),
    ('B006', 'Data Communications and Networking', 'Behrouz A. Forouzan', 2, 2),
    ('B007', 'Software Engineering: A Practitioner''s Approach', 'Roger S. Pressman', 4, 3),
    ('B008', 'Head First Design Patterns', 'Eric Freeman', 2, 2),
    ('B009', 'Effective Java', 'Joshua Bloch', 3, 3),
    ('B010', 'The Pragmatic Programmer', 'Andrew Hunt', 2, 2),
    ('B011', 'Data Structures Using C', 'Reema Thareja', 5, 4),
    ('B012', 'Discrete Mathematics and Its Applications', 'Kenneth H. Rosen', 3, 3);

-- ----------------------------------------------------------------------------
-- 2. MEMBERS (4 accounts, password = member123)
--    M004 is stored as SUSPENDED because his book is 101 days overdue;
--    the application re-checks this rule on every login and dashboard load.
-- ----------------------------------------------------------------------------
INSERT INTO members (member_id, name, password_hash, status) VALUES
    ('M001', 'Aarav Mehta', '$2a$10$85/zqDlV9EKoJOwg9NlAU.MQXphreIrDswmIZ660pIIP879HWNDl2', 'ACTIVE'),
    ('M002', 'Diya Kapoor', '$2a$10$85/zqDlV9EKoJOwg9NlAU.MQXphreIrDswmIZ660pIIP879HWNDl2', 'ACTIVE'),
    ('M003', 'Rohan Sinha', '$2a$10$85/zqDlV9EKoJOwg9NlAU.MQXphreIrDswmIZ660pIIP879HWNDl2', 'ACTIVE'),
    ('M004', 'Sneha Nair', '$2a$10$85/zqDlV9EKoJOwg9NlAU.MQXphreIrDswmIZ660pIIP879HWNDl2', 'SUSPENDED');

-- ----------------------------------------------------------------------------
-- 3. LOANS
--    Loan period = 14 days, so due_date = issue_date + 14.
-- ----------------------------------------------------------------------------
INSERT INTO loans (loan_id, member_id, book_id, issue_date, due_date,
                   return_date, returned, renewed, fine) VALUES
    -- 1) Active and on time: issued 3 days ago, due in 11 days
    (1, 'M001', 'B003', CURDATE() - INTERVAL 3 DAY,   CURDATE() + INTERVAL 11 DAY, NULL, FALSE, FALSE, 0.00),
    -- 2) Active and 5 days overdue -> fine Rs 10
    (2, 'M002', 'B007', CURDATE() - INTERVAL 19 DAY,  CURDATE() - INTERVAL 5 DAY,  NULL, FALSE, FALSE, 0.00),
    -- 3) Active and 101 days overdue -> M004 is auto-suspended
    (3, 'M004', 'B011', CURDATE() - INTERVAL 115 DAY, CURDATE() - INTERVAL 101 DAY, NULL, FALSE, FALSE, 0.00),
    -- 4) History: returned 2 days before the due date, no fine
    (4, 'M003', 'B001', CURDATE() - INTERVAL 30 DAY,  CURDATE() - INTERVAL 16 DAY,
     CURDATE() - INTERVAL 18 DAY, TRUE, FALSE, 0.00),
    -- 5) History: returned 3 days late -> fine Rs 6
    (5, 'M001', 'B005', CURDATE() - INTERVAL 40 DAY,  CURDATE() - INTERVAL 26 DAY,
     CURDATE() - INTERVAL 23 DAY, TRUE, FALSE, 6.00);

-- ----------------------------------------------------------------------------
-- 4. SETTINGS - empty simulated date means "use the real system date"
-- ----------------------------------------------------------------------------
INSERT INTO app_settings (setting_key, setting_value) VALUES
    ('simulated_today', '');

-- Make sure the next loan created by the application gets id 6.
ALTER TABLE loans AUTO_INCREMENT = 6;
