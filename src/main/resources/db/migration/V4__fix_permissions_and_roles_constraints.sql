-- =========================================================
-- FIX PERMISSIONS UNIQUE INDEX (TYPO)
-- =========================================================

-- Eliminar índice mal nombrado si existe
DROP INDEX IF EXISTS ux_permissions_codeq;

-- Crear el índice correcto (idempotente)
CREATE UNIQUE INDEX IF NOT EXISTS ux_permissions_code
    ON permissions (code)
    WHERE deleted_at IS NULL;


-- =========================================================
-- FIX ROLES TENANT COLUMN (SAFE)
-- =========================================================

-- Asegurar que la columna tenant_id exista
ALTER TABLE roles
ADD COLUMN IF NOT EXISTS tenant_id UUID;

-- Asegurar NOT NULL solo si aún no está definido
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'roles'
          AND column_name = 'tenant_id'
          AND is_nullable = 'YES'
    ) THEN
        ALTER TABLE roles
        ALTER COLUMN tenant_id SET NOT NULL;
    END IF;
END$$;


-- =========================================================
-- FIX UNIQUE ROLE NAME PER TENANT
-- =========================================================

-- Eliminar índice global anterior si existía
DROP INDEX IF EXISTS ux_roles_name;

-- Crear índice correcto por tenant
CREATE UNIQUE INDEX IF NOT EXISTS ux_roles_name_per_tenant
    ON roles (tenant_id, name)
    WHERE deleted_at IS NULL;
