-- Create work_orders table
CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    work_order_number VARCHAR(100) UNIQUE NOT NULL,
    asset_id BIGINT NOT NULL REFERENCES assets(id),
    maintenance_plan_id BIGINT REFERENCES maintenance_plans(id),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    assigned_technician VARCHAR(150),
    priority VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    scheduled_date DATE,
    start_date DATE,
    completion_date DATE,
    estimated_hours NUMERIC(5,2),
    actual_hours NUMERIC(5,2),
    remarks VARCHAR(500),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_work_orders_asset_id ON work_orders(asset_id);
CREATE INDEX idx_work_orders_maintenance_plan_id ON work_orders(maintenance_plan_id);
CREATE INDEX idx_work_orders_status ON work_orders(status);
