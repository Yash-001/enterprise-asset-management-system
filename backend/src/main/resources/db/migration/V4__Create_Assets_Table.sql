-- Migration to create assets table
CREATE TABLE IF NOT EXISTS assets
(
    id BIGSERIAL PRIMARY KEY,

    asset_code VARCHAR(100) NOT NULL UNIQUE,

    asset_name VARCHAR(100) NOT NULL,

    description VARCHAR(500),

    purchase_date DATE NOT NULL,

    purchase_price DECIMAL(15, 2) NOT NULL,

    status VARCHAR(50) NOT NULL,

    department VARCHAR(100) NOT NULL,

    location VARCHAR(100) NOT NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_by VARCHAR(100) DEFAULT 'SYSTEM',

    active BOOLEAN NOT NULL DEFAULT TRUE
);
