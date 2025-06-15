CREATE TABLE IF NOT EXISTS tenant_role_permission
(
    record_id   BIGSERIAL PRIMARY KEY,
    tenant_role_record_id BIGINT NOT NULL,
    tenant_permission_record_id BIGINT NOT NULL,

    CONSTRAINT fk__tenant_role_permission__tenant_role FOREIGN KEY (tenant_role_record_id) REFERENCES tenant_roles (record_id) ON DELETE CASCADE,
    CONSTRAINT fk__tenant_role_permission__tenant_permission FOREIGN KEY (tenant_permission_record_id) REFERENCES tenant_permissions (record_id) ON DELETE CASCADE,
    CONSTRAINT uq_tenant_role_permission__role_permission UNIQUE (tenant_role_record_id, tenant_permission_record_id)
);