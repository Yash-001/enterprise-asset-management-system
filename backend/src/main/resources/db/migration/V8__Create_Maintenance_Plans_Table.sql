-- Migration to create maintenance_plans table
CREATE TABLE IF NOT EXISTS maintenance_plans
(
    id BIGSERIAL PRIMARY KEY,

    asset_id BIGINT NOT NULL,

    plan_code VARCHAR(100) NOT NULL UNIQUE,

    plan_name VARCHAR(150) NOT NULL,

    description VARCHAR(500),

    maintenance_type VARCHAR(50) NOT NULL,

    frequency_type VARCHAR(50) NOT NULL,

    frequency_value INTEGER NOT NULL,

    next_maintenance_date DATE NOT NULL,

    last_maintenance_date DATE,

    estimated_duration_hours NUMERIC(5, 2),

    priority VARCHAR(50) NOT NULL,

    active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    created_by VARCHAR(100) DEFAULT 'SYSTEM',

    updated_by VARCHAR(100) DEFAULT 'SYSTEM',

    CONSTRAINT fk_maintenance_plan_asset FOREIGN KEY (asset_id) REFERENCES assets(id)
);
