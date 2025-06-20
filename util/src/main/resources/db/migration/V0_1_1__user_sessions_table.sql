CREATE TABLE IF NOT EXISTS user_sessions
(
    id               BIGSERIAL PRIMARY KEY,
    user_id          BIGINT    NOT NULL,
    token            TEXT      NOT NULL UNIQUE,
    token_issue_at   TIMESTAMP NOT NULL,
    token_expire_at  TIMESTAMP NOT NULL,
    token_revoked_at TIMESTAMP DEFAULT NULL,
    created_at       TIMESTAMP NOT NULL,
    updated_at       TIMESTAMP NOT NULL,

    CONSTRAINT fk__user_sessions__user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);