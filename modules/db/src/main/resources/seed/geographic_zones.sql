delete from geographic_zones;
INSERT INTO geographic_zones
(code, name, level, parent) values
('Root', 'Root', 1, 1),
('Arusha', 'Arusha', 1, 2),
('Dodoma', 'Dodoma', 2, 2),
('Ngorongoro', 'Ngorongoro', 3, 3);
