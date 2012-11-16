INSERT INTO facility_type (id, name, description, level_id, nominal_max_month, nominal_eop, display_order, is_active)
VALUES
(1,'Warehouse', 'Central Supply Depot',null,3,0.5, 11, TRUE),
(2,'Lvl3 Hospital', 'State Hospital',null,3,0.5,1, TRUE),
(3,'Lvl2 Hospital', 'Regional Hospital',null,3,0.5,2, TRUE),
(4,'State Office', 'Management Office, no patient services',null,3,0.5,9, TRUE),
(5,'District Office', 'Management Office, no patient services',null,3,0.5,10, TRUE),
(6,'Health Center', 'Multi-program clinic',null,3,0.5,4, TRUE),
(7,'Health Post', 'Community Clinic',null,3,0.5,5, TRUE),
(8,'Lvl1 Hospital', 'District Hospital',null,3,0.5,3, TRUE),
(9,'Satellite Facility', 'Temporary service delivery point',null,1,0.25,6, FALSE),
(10,'CHW', 'Mobile worker based out of health center',null,1,0.25,7, TRUE),
(11,'DHMT', 'District Health Management Team',null,3,0.5,8, TRUE);



