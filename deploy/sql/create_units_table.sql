-- Table: units
CREATE TABLE IF NOT EXISTS units (
    unit_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    unit_name VARCHAR(255) NOT NULL,
    is_simple_unit BOOLEAN DEFAULT TRUE,
    
    CONSTRAINT fk_units_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_units_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_units_cmpid_name UNIQUE (cmpid, unit_name)
);