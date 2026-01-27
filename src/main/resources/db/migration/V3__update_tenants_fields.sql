-- Asegurar extensión UUID (por si no existe)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE tenants
    ADD COLUMN phone VARCHAR(50),
    ADD COLUMN country VARCHAR(50),
    ADD COLUMN billing_email VARCHAR(150),
    ADD COLUMN status VARCHAR(30) NOT NULL DEFAULT 'TRIAL',
    ADD COLUMN trial_ends_at TIMESTAMP,
    ADD COLUMN subscription_ends_at TIMESTAMP;

-- Asegurar unicidad del slug (clave para subdominio)
ALTER TABLE tenants
    ADD CONSTRAINT uk_tenants_slug UNIQUE (slug);
