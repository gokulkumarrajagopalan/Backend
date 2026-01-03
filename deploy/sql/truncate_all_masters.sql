-- ============================================================
-- TRUNCATE ALL MASTER TABLES - Clear All Data
-- ============================================================
-- Use this script to clear all master data before testing
-- Order matters due to potential foreign key constraints
-- ============================================================

-- Step 1: Disable Foreign Key Constraints (if using PostgreSQL)
-- For other databases, modify accordingly
-- SET session_replication_role = 'replica';  -- PostgreSQL

-- ============================================================
-- TRUNCATE DEPENDENT TABLES FIRST
-- ============================================================

-- Truncate Voucher Type (no dependencies)
TRUNCATE TABLE vouchertype CASCADE;

-- Truncate Units (no dependencies)
TRUNCATE TABLE units CASCADE;

-- Truncate Currency (no dependencies)
TRUNCATE TABLE currency CASCADE;

-- Truncate Tax Unit (no dependencies)
TRUNCATE TABLE taxunit CASCADE;

-- Truncate Cost Category (no dependencies)
TRUNCATE TABLE costcategory CASCADE;

-- Truncate Cost Center (no dependencies)
TRUNCATE TABLE costcenter CASCADE;

-- Truncate Godown (no dependencies)
TRUNCATE TABLE godown CASCADE;

-- Truncate Stock Category (no dependencies)
TRUNCATE TABLE stock_categories CASCADE;

-- Truncate Stock Group (no dependencies)
TRUNCATE TABLE stock_groups CASCADE;

-- Truncate Stock Item (may reference stock_groups, units)
TRUNCATE TABLE stock_items CASCADE;

-- Truncate Ledger (may reference groups)
TRUNCATE TABLE ledgers CASCADE;

-- Truncate Group (parent groups may reference this)
TRUNCATE TABLE groups CASCADE;

-- ============================================================
-- RE-ENABLE FOREIGN KEY CONSTRAINTS
-- ============================================================

-- SET session_replication_role = 'default';  -- PostgreSQL

-- ============================================================
-- VERIFICATION QUERIES
-- ============================================================

-- Check all tables are empty
SELECT 'units' as table_name, COUNT(*) as record_count FROM units
UNION ALL
SELECT 'vouchertype', COUNT(*) FROM vouchertype
UNION ALL
SELECT 'currency', COUNT(*) FROM currency
UNION ALL
SELECT 'taxunit', COUNT(*) FROM taxunit
UNION ALL
SELECT 'costcategory', COUNT(*) FROM costcategory
UNION ALL
SELECT 'costcenter', COUNT(*) FROM costcenter
UNION ALL
SELECT 'godown', COUNT(*) FROM godown
UNION ALL
SELECT 'stock_categories', COUNT(*) FROM stock_categories
UNION ALL
SELECT 'stock_groups', COUNT(*) FROM stock_groups
UNION ALL
SELECT 'stock_items', COUNT(*) FROM stock_items
UNION ALL
SELECT 'ledgers', COUNT(*) FROM ledgers
UNION ALL
SELECT 'groups', COUNT(*) FROM groups
ORDER BY table_name;

-- ============================================================
-- ALTERNATIVE: DELETE INSTEAD OF TRUNCATE
-- Use this if TRUNCATE causes issues with foreign keys
-- ============================================================

-- DELETE FROM vouchertype;
-- DELETE FROM units;
-- DELETE FROM currency;
-- DELETE FROM taxunit;
-- DELETE FROM costcategory;
-- DELETE FROM costcenter;
-- DELETE FROM godown;
-- DELETE FROM stock_categories;
-- DELETE FROM stock_groups;
-- DELETE FROM stock_items;
-- DELETE FROM ledgers;
-- DELETE FROM groups;

-- ============================================================
-- RESET AUTO-INCREMENT/SEQUENCES (if needed)
-- ============================================================

-- For MySQL:
-- ALTER TABLE units AUTO_INCREMENT = 1;
-- ALTER TABLE vouchertype AUTO_INCREMENT = 1;
-- ALTER TABLE currency AUTO_INCREMENT = 1;
-- ALTER TABLE taxunit AUTO_INCREMENT = 1;
-- ALTER TABLE costcategory AUTO_INCREMENT = 1;
-- ALTER TABLE costcenter AUTO_INCREMENT = 1;
-- ALTER TABLE godown AUTO_INCREMENT = 1;
-- ALTER TABLE stock_categories AUTO_INCREMENT = 1;
-- ALTER TABLE stock_groups AUTO_INCREMENT = 1;
-- ALTER TABLE stock_items AUTO_INCREMENT = 1;
-- ALTER TABLE ledgers AUTO_INCREMENT = 1;
-- ALTER TABLE groups AUTO_INCREMENT = 1;

-- For PostgreSQL:
-- SELECT setval(pg_get_serial_sequence('units', 'unit_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('vouchertype', 'vouchertype_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('currency', 'currency_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('taxunit', 'taxunit_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('costcategory', 'costcategory_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('costcenter', 'costcenter_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('godown', 'godown_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('stock_categories', 'stockcategory_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('stock_groups', 'stockgroup_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('stock_items', 'stockitem_id'), 1, false);
-- SELECT setval(pg_get_serial_sequence('ledgers', 'ledid'), 1, false);
-- SELECT setval(pg_get_serial_sequence('groups', 'grpid'), 1, false);

-- ============================================================
-- END OF TRUNCATE SCRIPT
-- ============================================================
