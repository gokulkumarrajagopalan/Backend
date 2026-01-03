-- ========================================
-- LEDGERS TABLE - TALLY DATA STRUCTURE
-- ========================================
-- This table stores ledger (account) details from Tally
-- Ledgers are individual accounts under groups in the chart of accounts

CREATE TABLE IF NOT EXISTS ledgers (
    -- ========== PRIMARY KEY & REFERENCES ==========
    ledid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    
    -- ========== TALLY IDENTIFIERS ==========
    masterid BIGINT NOT NULL,                    -- Unique ID from Tally (MASTERID)
    alterid BIGINT,                              -- Alteration ID from Tally (ALTERID)
    guid VARCHAR(100) UNIQUE NOT NULL,           -- Global Unique ID from Tally (GUID)
    
    -- ========== LEDGER BASIC INFORMATION ==========
    led_name VARCHAR(255) NOT NULL,              -- Name of the ledger (NAME)
    led_code VARCHAR(50),                        -- Optional code for the ledger
    led_alias VARCHAR(255),                      -- Alias/alternate name (ONLYALIAS)
    led_parent VARCHAR(255),                     -- Parent group name (PARENT)
    led_primary_group VARCHAR(255),              -- Top-level primary group
    led_description TEXT,                        -- Description (DESCRIPTION)
    led_note TEXT,                               -- Notes/Narration (NARRATION)
    
    -- ========== LEDGER CLASSIFICATION ==========
    is_revenue BOOLEAN DEFAULT FALSE,            -- Is this a revenue ledger? (ISREVENUE)
    is_reserved BOOLEAN DEFAULT FALSE,           -- Is this a Tally reserved/system ledger?
    reserved_name VARCHAR(255),                  -- Tally reserved name if applicable
    last_parent VARCHAR(255),                    -- Last parent in hierarchy (LASTPARENT)
    
    -- ========== LEDGER FEATURES ==========
    led_billwise_on BOOLEAN DEFAULT FALSE,       -- Enable bill-wise details (ISBILLWISEON)
    led_is_costcentre_on BOOLEAN DEFAULT FALSE,  -- Enable cost centre allocation (ISCOSTCENTRESON)
    
    -- ========== LEDGER HIERARCHY ==========
    parent_ledid BIGINT,                         -- Reference to parent ledger (if sub-ledger)
    grpid BIGINT,                                -- Reference to parent group
    level_number INTEGER DEFAULT 0,              -- Hierarchy level
    full_path VARCHAR(1000),                     -- Full hierarchy path
    parent_hierarchy TEXT,                       -- JSON array of parent hierarchy
    
    -- ========== CONTACT INFORMATION ==========
    led_mailing_name VARCHAR(255),               -- Mailing name (LEDMAILINGDETAILS.MAILINGNAME)
    led_address_1 VARCHAR(255),                  -- Address line 1
    led_address_2 VARCHAR(255),                  -- Address line 2
    led_address_3 VARCHAR(255),                  -- Address line 3
    led_address_4 VARCHAR(255),                  -- Address line 4
    led_state VARCHAR(100),                      -- State (LEDMAILINGDETAILS.STATE)
    led_country VARCHAR(100),                    -- Country (LEDMAILINGDETAILS.COUNTRY)
    led_pincode VARCHAR(20),                     -- Pincode (LEDMAILINGDETAILS.PINCODE)
    led_contact VARCHAR(100),                    -- Contact person name (LEDGERCONTACT)
    led_phone VARCHAR(20),                       -- Phone number (LEDGERPHONE)
    led_country_isd_code VARCHAR(10),            -- ISD code (LEDGERCOUNTRYISDCODE)
    led_mobile VARCHAR(20),                      -- Mobile number (LEDGERMOBILE)
    led_email VARCHAR(100),                      -- Email (EMAIL)
    led_website VARCHAR(255),                    -- Website (WEBSITE)
    
    -- ========== FINANCIAL INFORMATION ==========
    led_opening_balance DECIMAL(18, 2) DEFAULT 0.00,  -- Opening balance (OPENINGBALANCE)
    currency_name VARCHAR(50) DEFAULT '₹',            -- Currency symbol (CURRENCYNAME)
    income_tax_number VARCHAR(50),                    -- Income tax number (INCOMETAXNUMBER)
    
    -- ========== GST CONFIGURATION (INDIA) ==========
    gst_applicable BOOLEAN DEFAULT FALSE,             -- Is GST applicable?
    gst_registration_type VARCHAR(50),                -- Registration type: Regular, Composition, etc. (GSTREGISTRATIONTYPE)
    gst_registration_date DATE,                       -- GST registration date (APPLICABLEFROM in LEDGSTREGDETAILS)
    gst_gstin VARCHAR(15),                            -- GSTIN number (GSTIN)
    gst_is_freezone BOOLEAN DEFAULT FALSE,            -- Is in freezone? (derived from company or ledger)
    gst_state VARCHAR(100),                           -- GST state (STATE in LEDGSTREGDETAILS)
    gst_place_of_supply VARCHAR(100),                 -- Place of supply (PLACEOFSUPPLY)
    gst_transporter_id VARCHAR(50),                   -- Transporter ID (TRANSPORTERID)
    gst_is_other_territory_assessee BOOLEAN DEFAULT FALSE,  -- Other territory assessee
    gst_consider_purchase_for_export BOOLEAN DEFAULT FALSE, -- Consider purchase for export
    gst_is_transporter BOOLEAN DEFAULT FALSE,         -- Is transporter
    gst_is_common_party BOOLEAN DEFAULT FALSE,        -- Is common party
    
    -- ========== VAT CONFIGURATION (GCC) ==========
    vat_applicable BOOLEAN DEFAULT FALSE,             -- Is VAT applicable?
    vat_registration_type VARCHAR(50),                -- VAT registration type (VATDEALERTYPE)
    vat_registration_date DATE,                       -- VAT registration date (VATAPPLICABLEDATE)
    vat_tin_number VARCHAR(50),                       -- VAT TIN number (VATTINNUMBER)
    vat_is_freezone BOOLEAN DEFAULT FALSE,            -- Is in VAT freezone?
    
    -- ========== ADDITIONAL NAMES (Multilingual Support) ==========
    language_id INTEGER DEFAULT 1033,                 -- Language ID (1033 = English)
    alternate_names TEXT,                             -- JSON array of alternate names
    
    -- ========== STATUS & METADATA ==========
    is_active BOOLEAN DEFAULT TRUE,                   -- Is ledger currently active?
    is_deleted BOOLEAN DEFAULT FALSE,                 -- Soft delete flag
    sync_status VARCHAR(50) DEFAULT 'SYNCED',         -- Sync status with Tally
    last_sync_date TIMESTAMP,                         -- Last sync timestamp
    mailing_details_applicable_from DATE,             -- Applicable from date for mailing details
    gst_details_applicable_from DATE,                 -- Applicable from date for GST details
    
    -- ========== AUDIT FIELDS ==========
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- ========== CONSTRAINTS ==========
    CONSTRAINT fk_ledgers_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_ledgers_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT fk_ledgers_group FOREIGN KEY (grpid) REFERENCES groups(grpid) ON DELETE SET NULL,
    CONSTRAINT fk_ledgers_parent FOREIGN KEY (parent_ledid) REFERENCES ledgers(ledid) ON DELETE SET NULL,
    CONSTRAINT chk_level_number CHECK (level_number >= 0 AND level_number <= 10),
    CONSTRAINT unique_ledger_per_company UNIQUE (cmpid, led_name)
);

-- ========================================
-- INDEXES FOR PERFORMANCE
-- ========================================

-- Index on userid for filtering by user
CREATE INDEX IF NOT EXISTS idx_ledgers_userid ON ledgers(userid);

-- Index on cmpid for filtering by company
CREATE INDEX IF NOT EXISTS idx_ledgers_cmpid ON ledgers(cmpid);

-- Index on masterid for Tally sync operations
CREATE INDEX IF NOT EXISTS idx_ledgers_masterid ON ledgers(masterid);

-- Index on guid for Tally GUID lookups
CREATE INDEX IF NOT EXISTS idx_ledgers_guid ON ledgers(guid);

-- Index on led_name for search operations
CREATE INDEX IF NOT EXISTS idx_ledgers_name ON ledgers(led_name);

-- Index on led_parent for hierarchy queries
CREATE INDEX IF NOT EXISTS idx_ledgers_parent ON ledgers(led_parent);

-- Index on grpid for group-based queries
CREATE INDEX IF NOT EXISTS idx_ledgers_grpid ON ledgers(grpid);

-- Index on parent_ledid for hierarchy traversal
CREATE INDEX IF NOT EXISTS idx_ledgers_parent_ledid ON ledgers(parent_ledid);

-- Index on is_revenue for filtering revenue vs balance sheet ledgers
CREATE INDEX IF NOT EXISTS idx_ledgers_revenue ON ledgers(is_revenue);

-- Composite index for company + active ledgers
CREATE INDEX IF NOT EXISTS idx_ledgers_company_active ON ledgers(cmpid, is_active) WHERE is_deleted = FALSE;

-- Index on GST fields for GST-enabled companies
CREATE INDEX IF NOT EXISTS idx_ledgers_gstin ON ledgers(gst_gstin) WHERE gst_gstin IS NOT NULL;

-- Index on email for contact lookup
CREATE INDEX IF NOT EXISTS idx_ledgers_email ON ledgers(led_email) WHERE led_email IS NOT NULL;

-- Index on mobile for contact lookup
CREATE INDEX IF NOT EXISTS idx_ledgers_mobile ON ledgers(led_mobile) WHERE led_mobile IS NOT NULL;

-- ========================================
-- TRIGGER FOR UPDATED_AT
-- ========================================

CREATE OR REPLACE FUNCTION update_ledgers_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_ledgers_updated_at
    BEFORE UPDATE ON ledgers
    FOR EACH ROW
    EXECUTE FUNCTION update_ledgers_updated_at();

-- ========================================
-- SAMPLE QUERIES & USAGE
-- ========================================

-- Query 1: Get all ledgers for a company
-- SELECT * FROM ledgers WHERE cmpid = 1 AND is_deleted = FALSE;

-- Query 2: Get ledgers by parent group
-- SELECT * FROM ledgers WHERE led_parent = 'Cash-in-Hand' AND cmpid = 1;

-- Query 3: Get all customer ledgers (Sundry Debtors)
-- SELECT * FROM ledgers WHERE led_parent = 'Sundry Debtors' AND cmpid = 1;

-- Query 4: Get all supplier ledgers (Sundry Creditors)
-- SELECT * FROM ledgers WHERE led_parent = 'Sundry Creditors' AND cmpid = 1;

-- Query 5: Find a ledger by Tally GUID
-- SELECT * FROM ledgers WHERE guid = 'a2057a46-eb11-4993-a7d5-ce213662fecf-0000001f';

-- Query 6: Get ledgers with bill-wise enabled
-- SELECT * FROM ledgers WHERE led_billwise_on = TRUE AND cmpid = 1;

-- Query 7: Get ledgers with GST details
-- SELECT led_name, gst_gstin, gst_registration_type, gst_state 
-- FROM ledgers WHERE gst_applicable = TRUE AND cmpid = 1;

-- Query 8: Get ledgers with opening balance
-- SELECT led_name, led_opening_balance, currency_name 
-- FROM ledgers WHERE led_opening_balance != 0 AND cmpid = 1;

-- Query 9: Search ledgers by name or email
-- SELECT * FROM ledgers 
-- WHERE cmpid = 1 AND (
--     LOWER(led_name) LIKE LOWER('%search_term%') OR 
--     LOWER(led_email) LIKE LOWER('%search_term%')
-- ) AND is_deleted = FALSE;

-- Query 10: Get ledger hierarchy with group
-- SELECT l.led_name, l.led_parent, g.grp_name, l.full_path
-- FROM ledgers l
-- LEFT JOIN groups g ON l.grpid = g.grpid
-- WHERE l.cmpid = 1 AND l.is_deleted = FALSE
-- ORDER BY l.full_path;

-- ========================================
-- NOTES
-- ========================================
-- 1. led_parent stores the parent group name from Tally
-- 2. parent_ledid is for sub-ledgers (ledger under another ledger)
-- 3. grpid links to the parent group in the groups table
-- 4. masterid is Tally's unique identifier for the ledger
-- 5. alterid tracks modifications in Tally
-- 6. guid is Tally's global unique identifier (format: <company-guid>-<master-id-hex>)
-- 7. is_revenue = TRUE for P&L ledgers, FALSE for Balance Sheet ledgers
-- 8. GST fields are applicable only for Indian companies
-- 9. VAT fields are applicable only for GCC countries
-- 10. parent_hierarchy stores the full hierarchy as JSON array
-- 11. led_billwise_on enables bill-by-bill tracking for receivables/payables
-- 12. led_is_costcentre_on enables cost centre allocation for this ledger
-- 13. Company-specific GUID format: CMP{companyId}-{tallyGuid}
-- 14. Unique constraint on (cmpid, led_name) ensures no duplicate names per company
