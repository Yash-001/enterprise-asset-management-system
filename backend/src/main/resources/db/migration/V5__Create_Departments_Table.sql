-- Migration to create departments table
CREATE TABLE IF NOT EXISTS departments
(
    id BIGSERIAL PRIMARY KEY,

    department_code VARCHAR(100) NOT NULL UNIQUE,

    department_name VARCHAR(100) NOT NULL,

    manager VARCHAR(100),

    description VARCHAR(500),

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_by VARCHAR(100) DEFAULT 'SYSTEM'
);
