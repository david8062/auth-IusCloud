-- Replace first_name and last_name with a single username field.
-- Login now accepts email or username.

ALTER TABLE users DROP COLUMN IF EXISTS first_name;
ALTER TABLE users DROP COLUMN IF EXISTS last_name;

ALTER TABLE users ADD COLUMN IF NOT EXISTS username VARCHAR(100);
UPDATE users SET username = split_part(email, '@', 1) WHERE username IS NULL;
ALTER TABLE users ALTER COLUMN username SET NOT NULL;

CREATE UNIQUE INDEX ux_users_username_per_tenant ON users (tenant_id, username) WHERE deleted_at IS NULL;
