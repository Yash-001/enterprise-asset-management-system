-- Create purchase_orders table
CREATE TABLE purchase_orders (
    id BIGSERIAL PRIMARY KEY,
    po_number VARCHAR(100) NOT NULL UNIQUE,
    vendor_id BIGINT NOT NULL REFERENCES vendors(id) ON DELETE CASCADE,
    status VARCHAR(50) NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expected_delivery_date TIMESTAMP,
    total_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    remarks VARCHAR(500),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM',
    updated_by VARCHAR(100) NOT NULL DEFAULT 'SYSTEM'
);

-- Create purchase_order_items table
CREATE TABLE purchase_order_items (
    id BIGSERIAL PRIMARY KEY,
    purchase_order_id BIGINT NOT NULL REFERENCES purchase_orders(id) ON DELETE CASCADE,
    spare_part_id BIGINT NOT NULL REFERENCES spare_parts(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL,
    unit_price NUMERIC(10,2) NOT NULL,
    line_total NUMERIC(15,2) NOT NULL
);

CREATE INDEX idx_purchase_orders_number ON purchase_orders(po_number);
CREATE INDEX idx_purchase_orders_vendor ON purchase_orders(vendor_id);
CREATE INDEX idx_purchase_order_items_po ON purchase_order_items(purchase_order_id);
CREATE INDEX idx_purchase_order_items_part ON purchase_order_items(spare_part_id);
