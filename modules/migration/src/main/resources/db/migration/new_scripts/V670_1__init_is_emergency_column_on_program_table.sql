ALTER TABLE programs ADD COLUMN isEmergency BOOLEAN DEFAULT false;
UPDATE programs SET isEmergency = TRUE WHERE code != 'TARV' AND parentId IS NOT NULL;