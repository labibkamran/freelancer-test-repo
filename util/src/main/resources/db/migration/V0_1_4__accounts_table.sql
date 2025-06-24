CREATE TABLE IF NOT EXISTS accounts
(
    id                  BIGSERIAL PRIMARY KEY,
    account_name        VARCHAR(255) NOT NULL,
    account_description TEXT,
    account_number      VARCHAR(10),
    country_code        VARCHAR(2),
    created_at          TIMESTAMP    NOT NULL,
    updated_at          TIMESTAMP    NOT NULL
);

CREATE INDEX idx__accounts__account_number ON accounts (account_number);