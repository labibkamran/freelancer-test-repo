CREATE TABLE customers
(
    id                  BIGSERIAL PRIMARY KEY,
    tenant_id           BIGINT       NOT NULL,
    organization_number VARCHAR(50),
    name                VARCHAR(255) NOT NULL,
    type                VARCHAR(25)  NOT NULL,
    private_customer    BOOLEAN DEFAULT false NOT NULL,

    CONSTRAINT fk__customers__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE INDEX idx__customers__tenant_id ON customers (tenant_id);
CREATE INDEX idx__customers__name ON customers (name);