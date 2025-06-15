CREATE TABLE IF NOT EXISTS role_permission
(
    record_id   BIGSERIAL PRIMARY KEY,
    role_record_id BIGINT NOT NULL,
    permission_record_id BIGINT NOT NULL,

    CONSTRAINT fk_role FOREIGN KEY (role_record_id) REFERENCES roles (record_id) ON DELETE CASCADE,
    CONSTRAINT fk_permission FOREIGN KEY (permission_record_id) REFERENCES permissions (record_id) ON DELETE CASCADE,
    CONSTRAINT uq_role_permission UNIQUE (role_record_id, permission_record_id)
);