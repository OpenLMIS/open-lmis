ALTER TABLE users ALTER COLUMN email drop NOT NULL;

ALTER TABLE users ADD COLUMN ismobileuser boolean DEFAULT false;