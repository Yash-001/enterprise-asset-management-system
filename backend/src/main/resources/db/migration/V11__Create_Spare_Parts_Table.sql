-- Create spare_parts table
CREATE TABLE spare_parts (
    id BIGSERIAL PRIMARY KEY,
    part_number VARCHAR(100) UNIQUE NOT NULL,
    part_name VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    manufacturer VARCHAR(150),
    category VARCHAR(100),
    unit_of_measure VARCHAR(50),
    minimum_stock INTEGER NOT NULL DEFAULT 0,
    maximum_stock INTEGER NOT NULL DEFAULT 0,
    current_stock INTEGER NOT NULL DEFAULT 0,
    unit_cost NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    supplier_id BIGINT,
    location_id BIGINT REFERENCES locations(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL
);

CREATE INDEX idx_spare_parts_part_number ON spare_parts(part_number);
CREATE INDEX idx_spare_parts_category ON spare_parts(category);
CREATE INDEX idx_spare_parts_location_id ON spare_parts(location_id);
