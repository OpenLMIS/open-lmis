delete from geographic_zones;
INSERT INTO geographic_zones
(code, name, level, parent) values
('Root', 'Root', (SELECT id FROM geographic_levels WHERE code = 'country'), (SELECT id FROM geographic_levels WHERE code = 'country')),
('Arusha', 'Arusha',(SELECT id FROM geographic_levels WHERE code = 'state'), (SELECT id FROM geographic_levels WHERE code = 'country')),
('Dodoma', 'Dodoma', (SELECT id FROM geographic_levels WHERE code = 'district'), (SELECT id FROM geographic_levels WHERE code = 'state')),
('Ngorongoro', 'Ngorongoro', (SELECT id FROM geographic_levels WHERE code = 'city'), (SELECT id FROM geographic_levels WHERE code = 'district'));
