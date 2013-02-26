delete from geographic_zones;
INSERT INTO geographic_zones
(id, code, name, level, parent) values
(1, 'Arusha', 'Arusha', 1, 1),
(2, 'Dodoma', 'Dodoma', 2, 1),
(3, 'Ngorongoro', 'Ngorongoro', 3, 2);
