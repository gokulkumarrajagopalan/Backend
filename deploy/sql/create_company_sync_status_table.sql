-- Table: company_sync_status
CREATE TABLE IF NOT EXISTS company_sync_status (
    id BIGSERIAL PRIMARY KEY,
    cmp_id BIGINT NOT NULL UNIQUE,
    last_alter_id BIGINT NOT NULL DEFAULT 0,
    last_sync_time TIMESTAMP,
    entity_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_company_sync_cmp FOREIGN KEY (cmp_id) REFERENCES companies(cmpid) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_company_sync_cmp_id ON company_sync_status(cmp_id);