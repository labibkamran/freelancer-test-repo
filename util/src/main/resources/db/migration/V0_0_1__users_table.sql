CREATE TABLE IF NOT EXISTS users
(
    record_id     BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    is_enabled    BOOLEAN               DEFAULT TRUE,
    is_locked     BOOLEAN               DEFAULT FALSE,
    last_login_at TIMESTAMP             DEFAULT NULL,
    created_at    TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at    TIMESTAMP    NULL
);

CREATE INDEX idx_users_email ON users (email);