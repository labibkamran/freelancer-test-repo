CREATE TABLE IF NOT EXISTS user_tenant_roles
(
    record_id             BIGSERIAL PRIMARY KEY,
    user_record_id        BIGINT      NOT NULL,
    tenant_record_id      BIGINT      NOT NULL,
    tenant_role_record_id BIGINT      NOT NULL,
    created_at            TIMESTAMP   NOT NULL,
    updated_at            TIMESTAMP   NOT NULL,

    CONSTRAINT fk__user_tenant_roles__user FOREIGN KEY (user_record_id) REFERENCES users (record_id) ON DELETE CASCADE,
    CONSTRAINT fk__user_tenant_roles__tenant FOREIGN KEY (tenant_record_id) REFERENCES tenants (record_id) ON DELETE CASCADE,
    CONSTRAINT fk__user_tenant_roles__role FOREIGN KEY (tenant_role_record_id) REFERENCES tenant_roles (record_id) ON DELETE CASCADE,
    CONSTRAINT uq__user_tenant_roles__user_tenant UNIQUE (user_record_id, tenant_record_id, tenant_role_record_id)
);

CREATE INDEX idx__user_tenant_roles__composite ON user_tenant_roles (user_record_id, tenant_record_id, tenant_role_record_id);