CREATE TABLE IF NOT EXISTS tenant_role_permission
(
    id                   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_role_id       BIGINT NOT NULL,
    tenant_permission_id BIGINT NOT NULL,

    CONSTRAINT fk__tenant_role_permission__tenant_role FOREIGN KEY (tenant_role_id) REFERENCES tenant_roles (id) ON DELETE CASCADE,
    CONSTRAINT fk__tenant_role_permission__tenant_permission FOREIGN KEY (tenant_permission_id) REFERENCES tenant_permissions (id) ON DELETE CASCADE,
    CONSTRAINT uq_tenant_role_permission__role_permission UNIQUE (tenant_role_id, tenant_permission_id)
);