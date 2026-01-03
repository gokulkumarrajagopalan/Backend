-- Table: vouchertype
CREATE TABLE IF NOT EXISTS vouchertype (
    vouchertype_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    name VARCHAR(255) NOT NULL,
    parent VARCHAR(255),
    reservedname VARCHAR(255),
    numberingmethod VARCHAR(64),
    preventduplicates CHAR(1), -- 'Y' or 'N'
    isdeemedpositive CHAR(1),  -- 'Y' or 'N'
    affectstotal CHAR(1),      -- 'Y' or 'N'
    printaftersave CHAR(1),    -- 'Y' or 'N'
    languageid INT,
    
    CONSTRAINT fk_vouchertype_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_vouchertype_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_vouchertype_cmpid_name UNIQUE (cmpid, name)
);
