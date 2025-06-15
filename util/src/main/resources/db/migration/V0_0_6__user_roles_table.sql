CREATE TABLE IF NOT EXISTS user_roles
(
    record_id   BIGSERIAL PRIMARY KEY,
    role_record_id BIGINT NOT NULL,
    user_record_id BIGINT NOT NULL,

    CONSTRAINT fk__user_roles__role FOREIGN KEY (role_record_id) REFERENCES roles (record_id) ON DELETE CASCADE,
    CONSTRAINT fk__user_roles__user FOREIGN KEY (user_record_id) REFERENCES users (record_id) ON DELETE CASCADE,
    CONSTRAINT uq__user_roles__role_user UNIQUE (role_record_id, user_record_id)
);