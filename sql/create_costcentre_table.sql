-- Table: costcentre
CREATE TABLE IF NOT EXISTS costcentre (
    costcentre_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(255),
    reservedname VARCHAR(255),
    languageid INT,
    
    CONSTRAINT fk_costcentre_user FOREIGN KEY (userid) REFERENCES users(userid) ON DELETE CASCADE,
    CONSTRAINT fk_costcentre_company FOREIGN KEY (cmpid) REFERENCES companies(cmpid) ON DELETE CASCADE,
    CONSTRAINT uk_costcentre_cmpid_name UNIQUE (cmpid, name)
);
