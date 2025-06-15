CREATE TABLE IF NOT EXISTS user_tenant_roles
(
    record_id        BIGSERIAL PRIMARY KEY,
    user_record_id   BIGINT      NOT NULL,
    tenant_record_id BIGINT      NOT NULL,
    role_record_id   BIGINT      NOT NULL,
    is_active        BOOLEAN              DEFAULT TRUE,
    created_at       TIMESTAMP   NOT NULL DEFAULT now(),
    updated_at       TIMESTAMP   NULL,
    created_by       VARCHAR(50) NOT NULL,
    updated_by       VARCHAR(50),

    CONSTRAINT fk_user FOREIGN KEY (user_record_id) REFERENCES users (record_id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant FOREIGN KEY (tenant_record_id) REFERENCES tenants (record_id) ON DELETE CASCADE,
    CONSTRAINT fk_role FOREIGN KEY (role_record_id) REFERENCES roles_permissions (record_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_tenant UNIQUE (user_record_id, tenant_record_id)
);

CREATE INDEX idx_user_tenant_roles_composite ON user_tenant_roles (user_record_id, tenant_record_id, role_record_id);