-- =========================
-- PERMISSIONS (GLOBAL)
-- =========================
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    code VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE UNIQUE INDEX ux_permissions_code
    ON permissions (code)
    WHERE deleted_at IS NULL;

-- =========================
-- ROLES (PER TENANT)
-- =========================
ALTER TABLE roles
ADD COLUMN tenant_id UUID NOT NULL;

ALTER TABLE roles
ADD CONSTRAINT fk_roles_tenant
    FOREIGN KEY (tenant_id)
    REFERENCES tenants (id);

CREATE UNIQUE INDEX ux_roles_name_per_tenant
    ON roles (tenant_id, name)
    WHERE deleted_at IS NULL;

-- =========================
-- ROLE_PERMISSIONS
-- =========================
CREATE TABLE role_permissions (
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission_id),

    CONSTRAINT fk_role_permissions_role
        FOREIGN KEY (role_id)
        REFERENCES roles (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_role_permissions_permission
        FOREIGN KEY (permission_id)
        REFERENCES permissions (id)
        ON DELETE CASCADE
);
