ALTER TABLE programs ADD COLUMN isEmergency BOOLEAN DEFAULT false;

UPDATE programs SET isEmergency = TRUE WHERE code IN (SELECT code FROM programs WHERE code != 'TARV' AND parentId IS NOT NULL);