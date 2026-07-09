-- Create stock_transactions table
CREATE TABLE stock_transactions (
    id BIGSERIAL PRIMARY KEY,
    spare_part_id BIGINT NOT NULL REFERENCES spare_parts(id),
    transaction_type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    unit_cost NUMERIC(10,2) NOT NULL DEFAULT 0.00,
    reference_type VARCHAR(100),
    reference_id BIGINT,
    remarks VARCHAR(500),
    performed_by VARCHAR(255) NOT NULL,
    transaction_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_stock_transactions_spare_part_id ON stock_transactions(spare_part_id);
CREATE INDEX idx_stock_transactions_type ON stock_transactions(transaction_type);
CREATE INDEX idx_stock_transactions_performed_by ON stock_transactions(performed_by);
