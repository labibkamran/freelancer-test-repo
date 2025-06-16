CREATE TABLE IF NOT EXISTS tenants
(
    record_id  BIGSERIAL PRIMARY KEY,
    id         VARCHAR(36)  NOT NULL UNIQUE,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL,
    created_by VARCHAR(50)  NOT NULL,
    updated_by VARCHAR(50)  NOT NULL
);