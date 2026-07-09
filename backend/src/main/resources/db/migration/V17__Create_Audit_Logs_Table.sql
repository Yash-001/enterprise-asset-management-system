CREATE TABLE audit_logs (
    id              BIGSERIAL       PRIMARY KEY,
    entity_name     VARCHAR(100)    NOT NULL,
    action          VARCHAR(50)     NOT NULL,
    before_value    TEXT,
    after_value     TEXT,
    performed_by    VARCHAR(150)    NOT NULL,
    performed_at    TIMESTAMP       NOT NULL DEFAULT NOW(),
    ip_address      VARCHAR(50),
    entity_id       BIGINT
);

CREATE INDEX idx_audit_logs_entity ON audit_logs (entity_name, entity_id);
CREATE INDEX idx_audit_logs_performed_by ON audit_logs (performed_by);
CREATE INDEX idx_audit_logs_performed_at ON audit_logs (performed_at);
CREATE INDEX idx_audit_logs_action ON audit_logs (action);
