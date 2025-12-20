-- Table: taxunit
CREATE TABLE taxunit (
    taxunit_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    name VARCHAR(255) NOT NULL,
    reservedname VARCHAR(255),
    languageid INT
);
