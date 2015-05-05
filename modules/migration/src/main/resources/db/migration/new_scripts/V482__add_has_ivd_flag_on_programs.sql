ALTER TABLE programs
  ADD enableIvdForm BOOLEAN NOT NULL DEFAULT(false);

INSERT INTO rights(name, righttype, description, displayOrder, displayNameKey)
VALUES ('MANAGE_IVD_SETTINGS', 'ADMIN', 'Manage IVD settings', 50, 'rights.manage.ivd.settings');