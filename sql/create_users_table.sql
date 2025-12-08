-- Create users table for TallyBackend
CREATE TABLE IF NOT EXISTS users (
    userid BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    licence_no BIGINT NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(255),
    enabled BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP ,
    created_by BIGINT,
    updated_by BIGINT,
    isactive BOOLEAN DEFAULT true,
    last_login TIMESTAMP,
    device_token VARCHAR(500),
    device_login_at TIMESTAMP,
    
    -- Composite unique key for email and licence_no
    CONSTRAINT uk_email_licence UNIQUE (email, licence_no)
);

