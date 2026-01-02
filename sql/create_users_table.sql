-- Create users table for TallyBackend
CREATE TABLE IF NOT EXISTS users (
    userid BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    licence_no BIGINT NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(255),
    enabled CHAR(1) DEFAULT 'Y' CHECK (enabled IN ('Y', 'N')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    isactive CHAR(1) DEFAULT 'Y' CHECK (isactive IN ('Y', 'N')),
    last_login TIMESTAMP,
    device_token VARCHAR(500),
    device_login_at TIMESTAMP,
    
    -- Email Verification Fields
    email_verified CHAR(1) DEFAULT 'N' CHECK (email_verified IN ('Y', 'N')),
    
    -- Mobile and OTP Fields
    mobile VARCHAR(20),
    mobile_verified CHAR(1) DEFAULT 'N' CHECK (mobile_verified IN ('Y', 'N')),
    otp_code VARCHAR(6),
    otp_expiry TIMESTAMP,
    otp_sent_at TIMESTAMP,
    otp_verified CHAR(1) DEFAULT 'N' CHECK (otp_verified IN ('Y', 'N')),
    otp_resend_count INT DEFAULT 0,
    otp_last_resend_at TIMESTAMP,
    
    -- Mobile SMS Limits (3 per 24 hours)
    sms_count_today INT DEFAULT 0,
    sms_last_sent_date DATE,
    
    -- Composite unique key for email and licence_no
    CONSTRAINT uk_email_licence UNIQUE (email, licence_no)
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_device_token ON users(device_token);
CREATE INDEX IF NOT EXISTS idx_users_email_licence ON users(email, licence_no);
