-- Roles do not use soft delete; remove those columns and replace
-- the partial unique index with a regular one.

ALTER TABLE roles DROP COLUMN IF EXISTS deleted_at;
ALTER TABLE roles DROP COLUMN IF EXISTS active;

DROP INDEX IF EXISTS ux_roles_name_per_tenant;
CREATE UNIQUE INDEX ux_roles_name_per_tenant ON roles (tenant_id, name);
