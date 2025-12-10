-- ========================================
-- GROUPS TABLE - TALLY DATA STRUCTURE
-- ========================================
-- This table stores accounting group hierarchy from Tally
-- Groups define the classification of ledgers (Chart of Accounts)

CREATE TABLE IF NOT EXISTS groups (
    -- ========== PRIMARY KEY & REFERENCES ==========
    grpid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    
    -- ========== TALLY IDENTIFIERS ==========
    masterid BIGINT NOT NULL,                    -- Unique ID from Tally (MASTERID)
    alterid BIGINT,                              -- Alteration ID from Tally (ALTERID)
    guid VARCHAR(100) UNIQUE NOT NULL,           -- Global Unique ID from Tally (GUID)
    
    -- ========== GROUP INFORMATION ==========
    grp_name VARCHAR(255) NOT NULL,              -- Name of the group (NAME)
    grp_code VARCHAR(50),                        -- Optional code for the group
    grp_alias VARCHAR(255),                      -- Alias/alternate name (ONLYALIAS)
    grp_parent VARCHAR(255),                     -- Parent group name (PARENT)
    grp_primary_group VARCHAR(255),              -- Top-level primary group
    grp_nature VARCHAR(50),                      -- Nature: Asset, Liability, Income, Expense
    
    -- ========== GROUP CLASSIFICATION ==========
    is_revenue BOOLEAN DEFAULT FALSE,            -- Is this a revenue group? (ISREVENUE)
    is_reserved BOOLEAN DEFAULT FALSE,           -- Is this a Tally reserved/system group?
    reserved_name VARCHAR(255),                  -- Tally reserved name if applicable
    
    -- ========== GROUP HIERARCHY ==========
    parent_grpid BIGINT,                         -- Reference to parent group in this table
    level_number INTEGER DEFAULT 0,              -- Hierarchy level (0=Primary, 1=Sub, etc.)
    full_path VARCHAR(1000),                     -- Full path like "Primary > Current Assets > Bank"
    
    -- ========== ADDITIONAL NAMES (Multilingual Support) ==========
    language_id INTEGER DEFAULT 1033,            -- Language ID (1033 = English)
    alternate_names TEXT,                        -- JSON array of alternate names
    
    -- ========== STATUS & METADATA ==========
    is_active BOOLEAN DEFAULT TRUE,              -- Is group currently active?
    is_deleted BOOLEAN DEFAULT FALSE,            -- Soft delete flag
    sync_status VARCHAR(50) DEFAULT 'SYNCED',    -- Sync status with Tally
    last_sync_date TIMESTAMP,                    -- Last sync timestamp
    
    -- ========== AUDIT FIELDS ==========
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- ========== CONSTRAINTS ==========
    CONSTRAINT fk_groups_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_groups_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT fk_groups_parent FOREIGN KEY (parent_grpid) REFERENCES groups(grpid) ON DELETE SET NULL,
    CONSTRAINT chk_level_number CHECK (level_number >= 0 AND level_number <= 10),
    CONSTRAINT unique_group_per_company UNIQUE (cmpid, grp_name)
);

-- ========================================
-- INDEXES FOR PERFORMANCE
-- ========================================

-- Index on userid for filtering by user
CREATE INDEX IF NOT EXISTS idx_groups_userid ON groups(userid);

-- Index on cmpid for filtering by company
CREATE INDEX IF NOT EXISTS idx_groups_cmpid ON groups(cmpid);

-- Index on masterid for Tally sync operations
CREATE INDEX IF NOT EXISTS idx_groups_masterid ON groups(masterid);

-- Index on guid for Tally GUID lookups
CREATE INDEX IF NOT EXISTS idx_groups_guid ON groups(guid);

-- Index on grp_name for search operations
CREATE INDEX IF NOT EXISTS idx_groups_name ON groups(grp_name);

-- Index on grp_parent for hierarchy queries
CREATE INDEX IF NOT EXISTS idx_groups_parent ON groups(grp_parent);

-- Index on parent_grpid for hierarchy traversal
CREATE INDEX IF NOT EXISTS idx_groups_parent_grpid ON groups(parent_grpid);

-- Index on is_revenue for filtering revenue vs balance sheet groups
CREATE INDEX IF NOT EXISTS idx_groups_revenue ON groups(is_revenue);

-- Composite index for company + active groups
CREATE INDEX IF NOT EXISTS idx_groups_company_active ON groups(cmpid, is_active) WHERE is_deleted = FALSE;

-- ========================================
-- TRIGGER FOR UPDATED_AT
-- ========================================

CREATE OR REPLACE FUNCTION update_groups_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_groups_updated_at
    BEFORE UPDATE ON groups
    FOR EACH ROW
    EXECUTE FUNCTION update_groups_updated_at();

-- ========================================
-- SAMPLE QUERIES & USAGE
-- ========================================

-- Query 1: Get all primary groups
-- SELECT * FROM groups WHERE grp_parent = '&#4; Primary' OR grp_parent IS NULL;

-- Query 2: Get group hierarchy for a company
-- WITH RECURSIVE group_tree AS (
--     SELECT grpid, grp_name, grp_parent, parent_grpid, 0 as level
--     FROM groups 
--     WHERE parent_grpid IS NULL AND cmpid = 1
--     UNION ALL
--     SELECT g.grpid, g.grp_name, g.grp_parent, g.parent_grpid, gt.level + 1
--     FROM groups g
--     INNER JOIN group_tree gt ON g.parent_grpid = gt.grpid
-- )
-- SELECT * FROM group_tree ORDER BY level, grp_name;

-- Query 3: Get all revenue groups (P&L groups)
-- SELECT * FROM groups WHERE is_revenue = TRUE AND cmpid = 1;

-- Query 4: Get all balance sheet groups (non-revenue)
-- SELECT * FROM groups WHERE is_revenue = FALSE AND cmpid = 1;

-- Query 5: Find a group by Tally GUID
-- SELECT * FROM groups WHERE guid = '219dc5e4-a79a-4909-86c1-2b1ac6bf2b08-00000016';

-- ========================================
-- NOTES
-- ========================================
-- 1. grp_parent stores the parent name from Tally (may include special chars like &#4;)
-- 2. parent_grpid is the foreign key to actual parent record in this table
-- 3. masterid is Tally's unique identifier for the group
-- 4. alterid tracks modifications in Tally
-- 5. guid is Tally's global unique identifier (format: <company-guid>-<master-id-hex>)
-- 6. is_revenue = TRUE for P&L groups, FALSE for Balance Sheet groups
-- 7. reserved_name indicates Tally system groups (cannot be deleted in Tally)
-- 8. level_number helps in hierarchy display (0 = Primary, 1 = Direct children, etc.)
