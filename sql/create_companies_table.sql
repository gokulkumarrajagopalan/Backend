
CREATE TABLE IF NOT EXISTS companies (
    -- ========== IDENTITY & REFERENCE ==========
    cmpid BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    company_guid VARCHAR(36) UNIQUE NOT NULL,
    
    -- ========== BASIC COMPANY INFORMATION ==========
    name VARCHAR(255) NOT NULL,
    mailing_name VARCHAR(255),
    pan_number VARCHAR(20),
    business_type VARCHAR(100),
    
    -- ========== COMPANY STATUS & METADATA ==========
    status VARCHAR(50) DEFAULT 'ACTIVE',
    imported_from VARCHAR(100),
    imported_date DATE,
    sync_status VARCHAR(50),
    last_sync_date TIMESTAMP,
    
    -- ========== ADDRESS DETAILS ==========
    address_line_1 VARCHAR(255) NOT NULL,
    address_line_2 VARCHAR(255),
    address_line_3 VARCHAR(255),
    address_line_4 VARCHAR(255),
    state VARCHAR(100),
    country VARCHAR(100) NOT NULL DEFAULT 'India',
    pincode VARCHAR(20),
    
    -- ========== CONTACT INFORMATION ==========
    telephone VARCHAR(20),
    mobile VARCHAR(20),
    fax VARCHAR(20),
    email VARCHAR(100),
    website VARCHAR(255),
    code VARCHAR(50),
    
    -- ========== FINANCIAL CONFIGURATION ==========
    financial_year_start DATE NOT NULL,
    books_start DATE NOT NULL,
    
    -- ========== CURRENCY CONFIGURATION ==========
    currency_symbol VARCHAR(5) DEFAULT '₹',
    currency_formal_name VARCHAR(50) DEFAULT 'INR',
    currency_decimal_places INTEGER DEFAULT 2,
    
    -- ========== GST CONFIGURATION (INDIA) ==========
    gst_applicable_date DATE,
    gst_state VARCHAR(100),
    gst_type VARCHAR(50),          -- VALUES: 'Regular', 'Composition', 'Casual'
    gstin VARCHAR(15),
    gst_freezone BOOLEAN DEFAULT FALSE,
    gst_einvoice_applicable BOOLEAN DEFAULT FALSE,
    gst_eway_bill_applicable BOOLEAN DEFAULT FALSE,
    
    -- ========== VAT CONFIGURATION (GCC) ==========
    vat_emirate VARCHAR(100),
    vat_applicable_date DATE,
    vat_registration_number VARCHAR(50),
    vat_account_id VARCHAR(50),
    vat_freezone BOOLEAN DEFAULT FALSE,
    
    -- ========== FEATURE FLAGS ==========
    billwise_enabled BOOLEAN DEFAULT TRUE,
    costcentre_enabled BOOLEAN DEFAULT FALSE,
    batch_enabled BOOLEAN DEFAULT FALSE,
    use_discount_column BOOLEAN DEFAULT TRUE,
    use_actual_column BOOLEAN DEFAULT FALSE,
    payroll_enabled BOOLEAN DEFAULT FALSE,
    
    -- ========== AUDIT FIELDS ==========
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- ========== CONSTRAINTS & INDEXES ==========
    CONSTRAINT chk_currency_decimals CHECK (currency_decimal_places BETWEEN 0 AND 4),
    CONSTRAINT chk_financial_dates CHECK (books_start >= financial_year_start),
    CONSTRAINT fk_company_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE
);

-- Create index on userid for faster queries when fetching companies by user
CREATE INDEX IF NOT EXISTS idx_companies_userid ON companies(userid);