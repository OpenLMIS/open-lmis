-- This SQL script seeds the database with "extra" seed data; data that is seeded into the database in order to get the 
-- build to pass.

--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

SET search_path = public, pg_catalog;

--
-- Data for Name: dosage_units; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (1, 'mg', 1, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (2, 'ml', 2, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (3, 'each', 3, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (4, 'cc', 4, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (5, 'gm', 5, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (6, 'mcg', 6, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');
INSERT INTO dosage_units (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (7, 'IU', 7, '2016-02-03 14:37:39.598074', NULL, NULL, '2016-02-03 14:37:39.598074-08');


--
-- Name: dosage_units_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('dosage_units_id_seq', 7, true);


--
-- Data for Name: facility_operators; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO facility_operators (id, code, text, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (1, 'MoH', 'MoH', 1, '2016-02-03 14:37:39.605123', NULL, NULL, '2016-02-03 14:37:39.605123-08');
INSERT INTO facility_operators (id, code, text, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (2, 'NGO', 'NGO', 2, '2016-02-03 14:37:39.605123', NULL, NULL, '2016-02-03 14:37:39.605123-08');
INSERT INTO facility_operators (id, code, text, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (3, 'FBO', 'FBO', 3, '2016-02-03 14:37:39.605123', NULL, NULL, '2016-02-03 14:37:39.605123-08');
INSERT INTO facility_operators (id, code, text, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (4, 'Private', 'Private', 4, '2016-02-03 14:37:39.605123', NULL, NULL, '2016-02-03 14:37:39.605123-08');


--
-- Name: facility_operators_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('facility_operators_id_seq', 4, true);


--
-- Data for Name: facility_types; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (1, 'warehouse', 'Warehouse', 'Central Supply Depot', NULL, 3, 0.50, 11, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (2, 'lvl3_hospital', 'Lvl3 Hospital', 'State Hospital', NULL, 3, 0.50, 1, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (3, 'lvl2_hospital', 'Lvl2 Hospital', 'Regional Hospital', NULL, 3, 0.50, 2, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (4, 'state_office', 'State Office', 'Management Office, no patient services', NULL, 3, 0.50, 9, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (5, 'district_office', 'District Office', 'Management Office, no patient services', NULL, 3, 0.50, 10, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (6, 'health_center', 'Health Center', 'Multi-program clinic', NULL, 3, 0.50, 4, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (7, 'health_post', 'Health Post', 'Community Clinic', NULL, 3, 0.50, 5, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (8, 'lvl1_hospital', 'Lvl1 Hospital', 'District Hospital', NULL, 3, 0.50, 3, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (9, 'satellite_facility', 'Satellite Facility', 'Temporary service delivery point', NULL, 1, 0.25, 6, false, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (10, 'chw', 'CHW', 'Mobile worker based out of health center', NULL, 1, 0.25, 7, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');
INSERT INTO facility_types (id, code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active, createddate, createdby, modifiedby, modifieddate) VALUES (11, 'dhmt', 'DHMT', 'District Health Management Team', NULL, 3, 0.50, 8, true, '2016-02-03 14:37:39.611999', NULL, NULL, '2016-02-03 14:37:39.611999-08');


--
-- Name: facility_types_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('facility_types_id_seq', 11, true);


--
-- Data for Name: geographic_levels; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO geographic_levels (id, code, name, levelnumber, createddate, createdby, modifiedby, modifieddate) VALUES (1, 'country', 'Country', 1, '2016-02-03 14:37:39.613335', NULL, NULL, '2016-02-03 14:37:39.613335-08');
INSERT INTO geographic_levels (id, code, name, levelnumber, createddate, createdby, modifiedby, modifieddate) VALUES (2, 'state', 'State', 2, '2016-02-03 14:37:39.613335', NULL, NULL, '2016-02-03 14:37:39.613335-08');
INSERT INTO geographic_levels (id, code, name, levelnumber, createddate, createdby, modifiedby, modifieddate) VALUES (3, 'province', 'Province', 3, '2016-02-03 14:37:39.613335', NULL, NULL, '2016-02-03 14:37:39.613335-08');
INSERT INTO geographic_levels (id, code, name, levelnumber, createddate, createdby, modifiedby, modifieddate) VALUES (4, 'district', 'District', 4, '2016-02-03 14:37:39.613335', NULL, NULL, '2016-02-03 14:37:39.613335-08');


--
-- Name: geographic_levels_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('geographic_levels_id_seq', 4, true);


--
-- Data for Name: geographic_zones; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (1, 'Root', 'Root', 1, NULL, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.614363', NULL, '2016-02-03 14:37:39.614363');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (2, 'Mozambique', 'Mozambique', 1, NULL, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.61573', NULL, '2016-02-03 14:37:39.61573');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (3, 'Arusha', 'Arusha', 2, 1, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.616188', NULL, '2016-02-03 14:37:39.616188');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (4, 'Dodoma', 'Dodoma', 3, 3, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.616977', NULL, '2016-02-03 14:37:39.616977');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (5, 'Ngorongoro', 'Ngorongoro', 4, 4, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.617718', NULL, '2016-02-03 14:37:39.617718');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (6, 'Cabo Delgado Province', 'Cabo Delgado Province', 2, 2, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.618386', NULL, '2016-02-03 14:37:39.618386');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (7, 'Gaza Province', 'Gaza Province', 2, 2, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.618386', NULL, '2016-02-03 14:37:39.618386');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (8, 'Inhambane Province', 'Inhambane Province', 2, 2, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.618386', NULL, '2016-02-03 14:37:39.618386');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (9, 'Norte', 'Norte', 3, 6, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.619446', NULL, '2016-02-03 14:37:39.619446');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (10, 'Centro', 'Centro', 3, 7, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.620026', NULL, '2016-02-03 14:37:39.620026');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (11, 'Sul', 'Sul', 3, 8, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.620663', NULL, '2016-02-03 14:37:39.620663');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (12, 'District1', 'District1', 4, 9, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (13, 'District2', 'District2', 4, 9, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (14, 'District3', 'District3', 4, 9, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (15, 'District4', 'District4', 4, 10, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (16, 'District5', 'District5', 4, 10, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (17, 'District6', 'District6', 4, 10, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (18, 'District7', 'District7', 4, 11, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (19, 'District8', 'District8', 4, 11, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');
INSERT INTO geographic_zones (id, code, name, levelid, parentid, catchmentpopulation, latitude, longitude, createdby, createddate, modifiedby, modifieddate) VALUES (20, 'District9', 'District9', 4, 11, NULL, NULL, NULL, NULL, '2016-02-03 14:37:39.621196', NULL, '2016-02-03 14:37:39.621196');


--
-- Name: geographic_zones_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('geographic_zones_id_seq', 20, true);


--
-- Data for Name: product_forms; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (1, 'Tablet', 1, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (2, 'Capsule', 2, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (3, 'Bottle', 3, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (4, 'Vial', 4, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (5, 'Ampule', 5, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (6, 'Drops', 6, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (7, 'Powder', 7, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (8, 'Each', 8, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (9, 'Injectable', 9, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (10, 'Tube', 10, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (11, 'Solution', 11, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (12, 'Inhaler', 12, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (13, 'Patch', 13, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (14, 'Implant', 14, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (15, 'Sachet', 15, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (16, 'Device', 16, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');
INSERT INTO product_forms (id, code, displayorder, createddate, createdby, modifiedby, modifieddate) VALUES (17, 'Other', 17, '2016-02-03 14:37:39.623231', NULL, NULL, '2016-02-03 14:37:39.623231-08');


--
-- Name: product_forms_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_forms_id_seq', 17, true);


--
-- Data for Name: product_groups; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO product_groups (id, code, name, createdby, createddate, modifiedby, modifieddate) VALUES (1, 'PG', 'Product Group 1', NULL, '2016-02-03 14:37:39.624272', NULL, '2016-02-03 14:37:39.624272');


--
-- Name: product_groups_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('product_groups_id_seq', 1, true);


--
-- Data for Name: programs; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO programs (id, code, name, description, active, templateconfigured, regimentemplateconfigured, budgetingapplies, usesdar, push, sendfeed, createdby, createddate, modifiedby, modifieddate, isequipmentconfigured, hideskippedproducts, shownonfullsupplytab, enableskipperiod, enableivdform, usepriceschedule) VALUES (1, 'HIV', 'HIV', 'HIV', true, false, false, false, false, false, false, NULL, '2016-02-03 14:37:39.62539', NULL, '2016-02-03 14:37:39.62539', false, false, true, false, false, false);
INSERT INTO programs (id, code, name, description, active, templateconfigured, regimentemplateconfigured, budgetingapplies, usesdar, push, sendfeed, createdby, createddate, modifiedby, modifieddate, isequipmentconfigured, hideskippedproducts, shownonfullsupplytab, enableskipperiod, enableivdform, usepriceschedule) VALUES (2, 'ESS_MEDS', 'ESSENTIAL MEDICINES', 'ESSENTIAL MEDICINES', true, false, true, true, false, false, false, NULL, '2016-02-03 14:37:39.62539', NULL, '2016-02-03 14:37:39.62539', false, false, true, false, false, false);
INSERT INTO programs (id, code, name, description, active, templateconfigured, regimentemplateconfigured, budgetingapplies, usesdar, push, sendfeed, createdby, createddate, modifiedby, modifieddate, isequipmentconfigured, hideskippedproducts, shownonfullsupplytab, enableskipperiod, enableivdform, usepriceschedule) VALUES (3, 'TB', 'TB', 'TB', true, false, false, false, false, false, false, NULL, '2016-02-03 14:37:39.62539', NULL, '2016-02-03 14:37:39.62539', false, false, true, false, false, false);
INSERT INTO programs (id, code, name, description, active, templateconfigured, regimentemplateconfigured, budgetingapplies, usesdar, push, sendfeed, createdby, createddate, modifiedby, modifieddate, isequipmentconfigured, hideskippedproducts, shownonfullsupplytab, enableskipperiod, enableivdform, usepriceschedule) VALUES (4, 'MALARIA', 'MALARIA', 'MALARIA', true, false, false, true, false, false, false, NULL, '2016-02-03 14:37:39.62539', NULL, '2016-02-03 14:37:39.62539', false, false, true, false, false, false);
INSERT INTO programs (id, code, name, description, active, templateconfigured, regimentemplateconfigured, budgetingapplies, usesdar, push, sendfeed, createdby, createddate, modifiedby, modifieddate, isequipmentconfigured, hideskippedproducts, shownonfullsupplytab, enableskipperiod, enableivdform, usepriceschedule) VALUES (5, 'VACCINES', 'VACCINES', 'VACCINES', true, false, false, false, false, true, false, NULL, '2016-02-03 14:37:39.62539', NULL, '2016-02-03 14:37:39.62539', false, false, true, false, false, false);


--
-- Name: programs_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('programs_id_seq', 5, true);


--
-- Data for Name: regimen_categories; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO regimen_categories (id, code, name, displayorder, createdby, createddate, modifiedby, modifieddate) VALUES (1, 'ADULTS', 'Adults', 1, NULL, '2016-02-03 14:37:39.626711', NULL, '2016-02-03 14:37:39.626711');
INSERT INTO regimen_categories (id, code, name, displayorder, createdby, createddate, modifiedby, modifieddate) VALUES (2, 'PAEDIATRICS', 'Paediatrics', 2, NULL, '2016-02-03 14:37:39.626711', NULL, '2016-02-03 14:37:39.626711');


--
-- Name: regimen_categories_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('regimen_categories_id_seq', 2, true);


--
-- PostgreSQL database dump complete
--

