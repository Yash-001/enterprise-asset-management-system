CREATE TABLE documents (
    id              BIGSERIAL       PRIMARY KEY,
    file_name       VARCHAR(255)    NOT NULL,
    original_file_name VARCHAR(500) NOT NULL,
    content_type    VARCHAR(150)    NOT NULL,
    file_size       BIGINT          NOT NULL,
    storage_path    VARCHAR(1000)   NOT NULL,
    reference_type  VARCHAR(100)    NOT NULL,
    reference_id    BIGINT          NOT NULL,
    uploaded_by     VARCHAR(100),
    uploaded_at     TIMESTAMP       NOT NULL DEFAULT NOW(),
    active          BOOLEAN         NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_documents_reference ON documents (reference_type, reference_id);
CREATE INDEX idx_documents_active ON documents (active);
