CREATE TABLE IF NOT EXISTS user_tenant_roles
(
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT    NOT NULL,
    tenant_id      BIGINT    NOT NULL,
    tenant_role_id BIGINT    NOT NULL,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL,

    CONSTRAINT fk__user_tenant_roles__user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk__user_tenant_roles__tenant FOREIGN KEY (tenant_id) REFERENCES tenants (id) ON DELETE CASCADE,
    CONSTRAINT fk__user_tenant_roles__role FOREIGN KEY (tenant_role_id) REFERENCES tenant_roles (id) ON DELETE CASCADE,
    CONSTRAINT uq__user_tenant_roles__user_tenant UNIQUE (user_id, tenant_id, tenant_role_id)
);

CREATE INDEX idx__user_tenant_roles__composite ON user_tenant_roles (user_id, tenant_id, tenant_role_id);