CREATE TABLE IF NOT EXISTS companies
(
    id                  INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id           INTEGER       NOT NULL UNIQUE,
    organization_number VARCHAR(50)  NOT NULL,
    name                VARCHAR(255) NOT NULL,
    country_code        VARCHAR(2)   NOT NULL,
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL,

    CONSTRAINT fk__companies__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE INDEX idx__companies__tenant ON companies (tenant_id);