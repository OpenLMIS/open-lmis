delete from rights where name = 'MANAGE_GEOGRAPHIC_ZONES';
INSERT INTO rights(name, adminRight,description) VALUES
 ('MANAGE_GEOGRAPHIC_ZONES',TRUE,'Permission to manage geographic zones.');