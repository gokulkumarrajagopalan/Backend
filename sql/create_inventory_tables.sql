-- Stock Groups Table
CREATE TABLE stock_groups (
    stockgroup_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    masterid BIGINT,
    alterid BIGINT,
    guid VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    parent VARCHAR(255),
    reserved_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stockgroup_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_stockgroup_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_stockgroup_cmpid_name UNIQUE (cmpid, name)
);

CREATE INDEX idx_stockgroup_cmpid ON stock_groups(cmpid);
CREATE INDEX idx_stockgroup_userid ON stock_groups(userid);
CREATE INDEX idx_stockgroup_guid ON stock_groups(guid);

-- Stock Categories Table
CREATE TABLE stock_categories (
    stockcategory_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    masterid BIGINT,
    alterid BIGINT,
    guid VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    parent VARCHAR(255),
    reserved_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stockcategory_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_stockcategory_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_stockcategory_cmpid_name UNIQUE (cmpid, name)
);

CREATE INDEX idx_stockcategory_cmpid ON stock_categories(cmpid);
CREATE INDEX idx_stockcategory_userid ON stock_categories(userid);
CREATE INDEX idx_stockcategory_guid ON stock_categories(guid);

-- Godowns (Warehouses/Locations) Table
CREATE TABLE godowns (
    godown_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    masterid BIGINT,
    alterid BIGINT,
    guid VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    reserved_name VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_godown_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_godown_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_godown_cmpid_name UNIQUE (cmpid, name)
);

CREATE INDEX idx_godown_cmpid ON godowns(cmpid);
CREATE INDEX idx_godown_userid ON godowns(userid);
CREATE INDEX idx_godown_guid ON godowns(guid);

-- Stock Items Table
CREATE TABLE stock_items (
    stockitem_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    masterid BIGINT,
    alterid BIGINT,
    guid VARCHAR(100) UNIQUE,
    name VARCHAR(255) NOT NULL,
    parent VARCHAR(255),
    category VARCHAR(255),
    description TEXT,
    mailing_name VARCHAR(255),
    reserved_name VARCHAR(255),
    
    -- Units
    base_units VARCHAR(100),
    additional_units VARCHAR(100),
    
    -- Inventory
    opening_balance DECIMAL(19, 4),
    opening_value DECIMAL(19, 4),
    opening_rate DECIMAL(19, 4),
    
    -- Costing
    costing_method VARCHAR(50),
    valuation_method VARCHAR(50),
    
    -- GST
    gst_type_of_supply VARCHAR(50),
    hsn_code VARCHAR(50),
    
    -- Flags
    is_batch_wise_on BOOLEAN DEFAULT FALSE,
    is_cost_centers_on BOOLEAN DEFAULT FALSE,
    
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stockitem_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_stockitem_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_stockitem_cmpid_name UNIQUE (cmpid, name)
);

CREATE INDEX idx_stockitem_cmpid ON stock_items(cmpid);
CREATE INDEX idx_stockitem_userid ON stock_items(userid);
CREATE INDEX idx_stockitem_guid ON stock_items(guid);
CREATE INDEX idx_stockitem_parent ON stock_items(parent);
CREATE INDEX idx_stockitem_category ON stock_items(category);
CREATE INDEX idx_stockitem_hsn_code ON stock_items(hsn_code);
