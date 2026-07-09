-- Migration to create locations table
CREATE TABLE IF NOT EXISTS locations
(
    id BIGSERIAL PRIMARY KEY,

    location_code VARCHAR(100) NOT NULL UNIQUE,

    location_name VARCHAR(100) NOT NULL,

    building VARCHAR(100),

    floor VARCHAR(50),

    room VARCHAR(50),

    description VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_by VARCHAR(100) DEFAULT 'SYSTEM'
);
