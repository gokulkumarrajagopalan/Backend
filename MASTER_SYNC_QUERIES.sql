-- ============================================================
-- Master Sync - SQL Verification Queries
-- ============================================================
-- Use these queries to verify the sync is working correctly
-- ============================================================

-- ============================================================
-- 1. VERIFY MAX ALTERID PER MASTER (Company 9)
-- ============================================================

-- Units - Max AlterID
SELECT 'units' as entity, COALESCE(MAX(alterid), 0) as max_alterid
FROM units WHERE cmpid = 9;

-- StockCategory - Max AlterID
SELECT 'stockcategory' as entity, COALESCE(MAX(alterid), 0) as max_alterid
FROM stock_categories WHERE cmpid = 9;

-- StockGroup - Max AlterID
SELECT 'stockgroup' as entity, COALESCE(MAX(alterid), 0) as max_alterid
FROM stock_groups WHERE cmpid = 9;

-- StockItem - Max AlterID
SELECT 'stockitem' as entity, COALESCE(MAX(alterid), 0) as max_alterid
FROM stock_items WHERE cmpid = 9;

-- All masters at once
SELECT 
    'units' as entity,
    COALESCE(MAX(alterid), 0) as max_alterid,
    COUNT(*) as total_records
FROM units WHERE cmpid = 9
UNION ALL
SELECT 
    'stockcategory',
    COALESCE(MAX(alterid), 0),
    COUNT(*)
FROM stock_categories WHERE cmpid = 9
UNION ALL
SELECT 
    'stockgroup',
    COALESCE(MAX(alterid), 0),
    COUNT(*)
FROM stock_groups WHERE cmpid = 9
UNION ALL
SELECT 
    'stockitem',
    COALESCE(MAX(alterid), 0),
    COUNT(*)
FROM stock_items WHERE cmpid = 9
UNION ALL
SELECT 
    'groups',
    COALESCE(MAX(alterid), 0),
    COUNT(*)
FROM groups WHERE cmpid = 9
UNION ALL
SELECT 
    'ledgers',
    COALESCE(MAX(alterid), 0),
    COUNT(*)
FROM ledgers WHERE cmpid = 9
ORDER BY entity;

-- ============================================================
-- 2. VERIFY UPSERT LOGIC (cmpId + masterId)
-- ============================================================

-- Check Units for duplicates (should be none if UPSERT works)
SELECT cmpid, masterid, COUNT(*) as count
FROM units
WHERE cmpid = 9
GROUP BY cmpid, masterid
HAVING COUNT(*) > 1;

-- Check StockItem for duplicates
SELECT cmpid, masterid, COUNT(*) as count
FROM stock_items
WHERE cmpid = 9
GROUP BY cmpid, masterid
HAVING COUNT(*) > 1;

-- Check StockCategory for duplicates
SELECT cmpid, masterid, COUNT(*) as count
FROM stock_categories
WHERE cmpid = 9
GROUP BY cmpid, masterid
HAVING COUNT(*) > 1;

-- Check StockGroup for duplicates
SELECT cmpid, masterid, COUNT(*) as count
FROM stock_groups
WHERE cmpid = 9
GROUP BY cmpid, masterid
HAVING COUNT(*) > 1;

-- ============================================================
-- 3. VIEW SYNCED UNITS
-- ============================================================

SELECT 
    unit_id,
    userid,
    cmpid,
    masterid,
    alterid,
    guid,
    unit_name,
    original_name,
    is_simple_unit,
    reserved_name
FROM units
WHERE cmpid = 9
ORDER BY alterid DESC;

-- ============================================================
-- 4. VIEW SYNCED STOCK CATEGORIES
-- ============================================================

SELECT 
    stockcategory_id,
    userid,
    cmpid,
    masterid,
    alterid,
    guid,
    name,
    parent,
    reserved_name,
    created_at
FROM stock_categories
WHERE cmpid = 9
ORDER BY alterid DESC;

-- ============================================================
-- 5. VIEW SYNCED STOCK GROUPS
-- ============================================================

SELECT 
    stockgroup_id,
    userid,
    cmpid,
    masterid,
    alterid,
    guid,
    name,
    parent,
    reserved_name,
    created_at
FROM stock_groups
WHERE cmpid = 9
ORDER BY alterid DESC;

-- ============================================================
-- 6. VIEW SYNCED STOCK ITEMS
-- ============================================================

SELECT 
    stockitem_id,
    userid,
    cmpid,
    masterid,
    alterid,
    guid,
    name,
    parent,
    category,
    base_units,
    additional_units,
    created_at
FROM stock_items
WHERE cmpid = 9
ORDER BY alterid DESC;

-- ============================================================
-- 7. FIND RECORDS BY ALTERID RANGE
-- ============================================================

-- Units with AlterID > 200
SELECT * FROM units
WHERE cmpid = 9 AND alterid > 200
ORDER BY alterid;

-- StockItems with AlterID > 210
SELECT * FROM stock_items
WHERE cmpid = 9 AND alterid > 210
ORDER BY alterid;

-- ============================================================
-- 8. FIND RECORDS BY MASTERID (Reconciliation Check)
-- ============================================================

-- Find Unit by MasterID 206
SELECT * FROM units
WHERE cmpid = 9 AND masterid = 206;

-- Find StockItem by MasterID 212
SELECT * FROM stock_items
WHERE cmpid = 9 AND masterid = 212;

-- Find StockCategory by MasterID 208
SELECT * FROM stock_categories
WHERE cmpid = 9 AND masterid = 208;

-- ============================================================
-- 9. FIND RECORDS BY GUID
-- ============================================================

-- Units with Dollar GUID
SELECT * FROM units
WHERE guid = 'badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000ce';

-- StockItem with Us GUID
SELECT * FROM stock_items
WHERE guid = 'badaf5d1-7e2c-4282-8161-ab3ab85a12f8-000000d4';

-- ============================================================
-- 10. COMPLETENESS CHECK
-- ============================================================

-- Verify all synced records have required fields
SELECT 
    'units' as entity,
    COUNT(*) as total,
    COUNT(CASE WHEN masterid IS NULL THEN 1 END) as missing_masterid,
    COUNT(CASE WHEN guid IS NULL THEN 1 END) as missing_guid,
    COUNT(CASE WHEN alterid IS NULL THEN 1 END) as missing_alterid
FROM units WHERE cmpid = 9
UNION ALL
SELECT 
    'stockcategory',
    COUNT(*),
    COUNT(CASE WHEN masterid IS NULL THEN 1 END),
    COUNT(CASE WHEN guid IS NULL THEN 1 END),
    COUNT(CASE WHEN alterid IS NULL THEN 1 END)
FROM stock_categories WHERE cmpid = 9
UNION ALL
SELECT 
    'stockgroup',
    COUNT(*),
    COUNT(CASE WHEN masterid IS NULL THEN 1 END),
    COUNT(CASE WHEN guid IS NULL THEN 1 END),
    COUNT(CASE WHEN alterid IS NULL THEN 1 END)
FROM stock_groups WHERE cmpid = 9
UNION ALL
SELECT 
    'stockitem',
    COUNT(*),
    COUNT(CASE WHEN masterid IS NULL THEN 1 END),
    COUNT(CASE WHEN guid IS NULL THEN 1 END),
    COUNT(CASE WHEN alterid IS NULL THEN 1 END)
FROM stock_items WHERE cmpid = 9;

-- ============================================================
-- 11. SYNC PROGRESS TRACKING
-- ============================================================

-- Last sync status for Units
SELECT 
    'units' as entity,
    COUNT(*) as total_records,
    MAX(alterid) as last_alterid_synced,
    MAX(created_at) as last_sync_time
FROM units WHERE cmpid = 9;

-- Last sync status for all
SELECT 
    'units' as entity,
    COUNT(*) as total_records,
    COALESCE(MAX(alterid), 0) as last_alterid_synced,
    MAX(created_at) as last_sync_time
FROM units WHERE cmpid = 9
UNION ALL
SELECT 
    'stockcategory',
    COUNT(*),
    COALESCE(MAX(alterid), 0),
    MAX(created_at)
FROM stock_categories WHERE cmpid = 9
UNION ALL
SELECT 
    'stockgroup',
    COUNT(*),
    COALESCE(MAX(alterid), 0),
    MAX(created_at)
FROM stock_groups WHERE cmpid = 9
UNION ALL
SELECT 
    'stockitem',
    COUNT(*),
    COALESCE(MAX(alterid), 0),
    MAX(created_at)
FROM stock_items WHERE cmpid = 9
ORDER BY entity;

-- ============================================================
-- 12. DELETE TEST DATA (BE CAREFUL!)
-- ============================================================

-- DELETE from units WHERE cmpid = 9 AND alterid IN (208, 209);
-- DELETE from stock_categories WHERE cmpid = 9 AND alterid = 210;
-- DELETE from stock_groups WHERE cmpid = 9 AND alterid = 215;
-- DELETE from stock_items WHERE cmpid = 9 AND alterid IN (214, 219);

-- ============================================================
-- 13. CREATE INDEXES FOR PERFORMANCE
-- ============================================================

CREATE INDEX IF NOT EXISTS idx_units_cmpid_alterid 
ON units(cmpid, alterid);

CREATE INDEX IF NOT EXISTS idx_units_cmpid_masterid 
ON units(cmpid, masterid);

CREATE INDEX IF NOT EXISTS idx_stock_categories_cmpid_alterid 
ON stock_categories(cmpid, alterid);

CREATE INDEX IF NOT EXISTS idx_stock_categories_cmpid_masterid 
ON stock_categories(cmpid, masterid);

CREATE INDEX IF NOT EXISTS idx_stock_groups_cmpid_alterid 
ON stock_groups(cmpid, alterid);

CREATE INDEX IF NOT EXISTS idx_stock_groups_cmpid_masterid 
ON stock_groups(cmpid, masterid);

CREATE INDEX IF NOT EXISTS idx_stock_items_cmpid_alterid 
ON stock_items(cmpid, alterid);

CREATE INDEX IF NOT EXISTS idx_stock_items_cmpid_masterid 
ON stock_items(cmpid, masterid);

-- ============================================================
-- 14. EXPECTED TEST DATA AFTER SYNC
-- ============================================================

-- Expected counts:
-- units: 2 records (AlterID 208, 209)
-- stockcategory: 1 record (AlterID 210)
-- stockgroup: 1 record (AlterID 215)
-- stockitem: 2 records (AlterID 214, 219)

-- Expected max AlterIDs:
-- units: 209
-- stockcategory: 210
-- stockgroup: 215
-- stockitem: 219
