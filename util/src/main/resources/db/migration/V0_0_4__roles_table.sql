CREATE TABLE IF NOT EXISTS roles
(
    record_id   BIGSERIAL PRIMARY KEY,
    name        VARCHAR(128) NOT NULL,
    code        VARCHAR(128) NOT NULL,
    description VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP    NULL,
    created_by  VARCHAR(50)  NOT NULL,
    updated_by  VARCHAR(50)
);