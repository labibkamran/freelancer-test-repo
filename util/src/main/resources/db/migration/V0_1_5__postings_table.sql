CREATE TABLE IF NOT EXISTS postings
(
    id           BIGSERIAL PRIMARY KEY,
    account_id   BIGINT         NOT NULL,
    amount       DECIMAL(15, 2) NOT NULL,
    currency     VARCHAR(3)     NOT NULL,
    posting_date TIMESTAMP      NOT NULL,
    description  TEXT,
    tenant_id    BIGINT         NOT NULL,
    created_at   TIMESTAMP      NOT NULL,
    updated_at   TIMESTAMP      NOT NULL,

    CONSTRAINT fk__postings__account FOREIGN KEY (account_id) REFERENCES accounts (id) ON DELETE RESTRICT,
    CONSTRAINT fk__postings__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE
);

CREATE INDEX idx__postings__account_id ON postings (account_id);
CREATE INDEX idx__postings__tenant_id ON postings (tenant_id);
CREATE INDEX idx__postings__posting_date ON postings (posting_date);