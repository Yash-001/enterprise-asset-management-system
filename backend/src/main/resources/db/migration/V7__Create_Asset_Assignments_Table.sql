-- Migration to create asset_assignments table
CREATE TABLE IF NOT EXISTS asset_assignments
(
    id BIGSERIAL PRIMARY KEY,

    asset_id BIGINT NOT NULL,

    employee_id BIGINT NOT NULL,

    assigned_date DATE NOT NULL,

    expected_return_date DATE NOT NULL,

    returned_date DATE,

    remarks VARCHAR(500),

    status VARCHAR(50) NOT NULL,

    CONSTRAINT fk_assignment_asset FOREIGN KEY (asset_id) REFERENCES assets(id)
);
