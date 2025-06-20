-- Insert tenant permissions
INSERT INTO tenant_permissions (name, code, description, created_at, updated_at) VALUES
('All Read', 'all:read', 'Read access to all resources', NOW(), NOW()),
('All Write', 'all:write', 'Write access to all resources', NOW(), NOW()),
('Submit Hours', 'hours:submit', 'Submit time/hours entries', NOW(), NOW()),
('Submit Expenses', 'expenses:submit', 'Submit expense reports', NOW(), NOW());

-- Insert tenant roles
INSERT INTO tenant_roles (name, code, description, created_at, updated_at) VALUES
('Owner', 'OWNER', 'Full ownership access to all resources', NOW(), NOW()),
('Accountant', 'ACCOUNTANT', 'Full access to all accounting functions', NOW(), NOW()),
('Auditor', 'AUDITOR', 'Read-only access to all resources for auditing', NOW(), NOW()),
('Employee', 'EMPLOYEE', 'Limited access for basic employee functions', NOW(), NOW());

-- Link roles to permissions
-- Owner: all:read + all:write
INSERT INTO tenant_role_permission (tenant_role_id, tenant_permission_id) VALUES
((SELECT id FROM tenant_roles WHERE code = 'OWNER'), (SELECT id FROM tenant_permissions WHERE code = 'all:read')),
((SELECT id FROM tenant_roles WHERE code = 'OWNER'), (SELECT id FROM tenant_permissions WHERE code = 'all:write'));

-- Accountant: all:read + all:write
INSERT INTO tenant_role_permission (tenant_role_id, tenant_permission_id) VALUES
((SELECT id FROM tenant_roles WHERE code = 'ACCOUNTANT'), (SELECT id FROM tenant_permissions WHERE code = 'all:read')),
((SELECT id FROM tenant_roles WHERE code = 'ACCOUNTANT'), (SELECT id FROM tenant_permissions WHERE code = 'all:write'));

-- Auditor: all:read only
INSERT INTO tenant_role_permission (tenant_role_id, tenant_permission_id) VALUES
((SELECT id FROM tenant_roles WHERE code = 'AUDITOR'), (SELECT id FROM tenant_permissions WHERE code = 'all:read'));

-- Employee: hours:submit + expenses:submit
INSERT INTO tenant_role_permission (tenant_role_id, tenant_permission_id) VALUES
((SELECT id FROM tenant_roles WHERE code = 'EMPLOYEE'), (SELECT id FROM tenant_permissions WHERE code = 'hours:submit')),
((SELECT id FROM tenant_roles WHERE code = 'EMPLOYEE'), (SELECT id FROM tenant_permissions WHERE code = 'expenses:submit')); 