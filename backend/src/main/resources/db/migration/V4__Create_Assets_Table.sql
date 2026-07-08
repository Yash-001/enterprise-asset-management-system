-- Migration to create assets table with updated fields
CREATE TABLE IF NOT EXISTS assets
(
    id BIGSERIAL PRIMARY KEY,

    asset_code VARCHAR(100) NOT NULL UNIQUE,

    asset_name VARCHAR(100) NOT NULL,

    description VARCHAR(500),

    serial_number VARCHAR(100),

    manufacturer VARCHAR(100),

    model VARCHAR(100),

    purchase_date DATE NOT NULL,

    purchase_price DECIMAL(15, 2) NOT NULL,

    warranty_expiry DATE,

    status VARCHAR(50) NOT NULL,

    department_id BIGINT,

    location_id BIGINT,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_by VARCHAR(100) DEFAULT 'SYSTEM'
);
