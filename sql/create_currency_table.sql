-- Table: currency
CREATE TABLE currency (
    currency_id BIGSERIAL PRIMARY KEY,
    userid BIGINT NOT NULL,
    cmpid BIGINT NOT NULL,
    guid VARCHAR(64) NOT NULL,
    masterid BIGINT NOT NULL,
    alterid BIGINT,
    name VARCHAR(255) NOT NULL,
    symbol VARCHAR(16),
    formalname VARCHAR(255),
    decimalplaces INT,
    decimalseparator VARCHAR(8),
    showamountinwords CHAR(1), -- 'Y' or 'N'
    suffixsymbol CHAR(1),      -- 'Y' or 'N'
    spacebetweenamountandsymbol CHAR(1), -- 'Y' or 'N'
    languageid INT
);
