-- Table: taxunit
CREATE TABLE IF NOT EXISTS taxunit (
    taxunit_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    name VARCHAR(255) NOT NULL,
    reservedname VARCHAR(255),
    languageid INT,
    
    CONSTRAINT fk_taxunit_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_taxunit_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_taxunit_cmpid_name UNIQUE (cmpid, name)
);
