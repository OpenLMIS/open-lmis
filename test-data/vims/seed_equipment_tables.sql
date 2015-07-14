DO
$$
BEGIN

INSERT INTO equipment_cold_chain_equipment_designations (name)
VALUES ('Refrigerator & Freezer')
    ,('Refrigerator')
    ,('Freezer')
;

INSERT INTO equipment_cold_chain_equipment_pqs_status (name)
VALUES ('Approved')
    ,('Not Recommended')
    ,('Suspended')
;

INSERT INTO equipment_energy_types (name)
VALUES ('Electricity')
    ,('Gas')
    ,('Solar')
    ,('Kerosene')
;

INSERT INTO equipment_types (code, name, iscoldchain)
VALUES ('CCE1', 'Cold Chain Equipment', TRUE)
;

INSERT INTO equipments (name, equipmenttypeid, manufacturer, model, energytypeid)
VALUES ('Dometic / TCW 2000 SDD',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Dometic','TCW 2000 SDD',(SELECT id FROM equipment_energy_types WHERE name = 'Solar'))
    ,('Dometic / TCW 3000 SDD',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Dometic','TCW 3000 SDD',(SELECT id FROM equipment_energy_types WHERE name = 'Solar'))
    ,('Haier / HBD-286',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Haier','HBD-286',(SELECT id FROM equipment_energy_types WHERE name = 'Electricity'))
    ,('Sibir / V170 EK',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Sibir','V170 EK',(SELECT id FROM equipment_energy_types WHERE name = 'Kerosene'))
    ,('Vestfrost / VLS 400',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Vestfrost','VLS 400',(SELECT id FROM equipment_energy_types WHERE name = 'Electricity'))
    ,('Vestfrost / VLS 350',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'Vestfrost','VLS 350',(SELECT id FROM equipment_energy_types WHERE name = 'Electricity'))
    ,('WICR / WICR (30 Cubic Meters)',(SELECT id FROM equipment_types WHERE code = 'CCE1'),'WICR','WICR (30 Cubic Meters)',(SELECT id FROM equipment_energy_types WHERE name = 'Electricity'))
;

INSERT INTO equipment_cold_chain_equipments (equipmentid, designationid, refrigeratorcapacity, freezercapacity, pqsstatusid)
VALUES ((SELECT id FROM equipments WHERE name = 'Dometic / TCW 2000 SDD'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),99.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Dometic / TCW 3000 SDD'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),156.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Haier / HBD-286'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Freezer'),NULL,224.00,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Sibir / V170 EK'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator & Freezer'),55.00,36.00,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Not Recommended'))
    ,((SELECT id FROM equipments WHERE name = 'Vestfrost / VLS 400'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),216.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Vestfrost / VLS 350'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),196.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'WICR / WICR (30 Cubic Meters)'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),10000.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
;

INSERT INTO equipment_type_programs (programid, equipmenttypeid, displayorder)
VALUES ((SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM equipment_types WHERE code = 'CCE1'),0)
;

UPDATE equipment_operational_status
SET category = 'LAB'
;

INSERT INTO equipment_operational_status (name, displayorder, category, isbad)
VALUES ('Functional',1,'CCE',FALSE)
    ,('Functional But Not Installed',2,'CCE',FALSE)
    ,('Not Functional',3,'CCE',TRUE)
    ,('Waiting For Repair',1,'CCE Not Functional',FALSE)
    ,('Waiting For Spare Parts',2,'CCE Not Functional',FALSE)
    ,('Obsolete',3,'CCE Not Functional',TRUE)
;

UPDATE programs
SET push = FALSE
WHERE code = 'Vaccine'
;

ALTER SEQUENCE facility_types_id_seq RESTART WITH 16;

INSERT INTO facility_types (code, name, description, levelid, nominalmaxmonth, nominaleop, displayorder, active)
VALUES ('cvs', 'Central Vaccine Store', 'Central Vaccine Store', 1, 6, 3.00, 16, TRUE)
    ,('rvs', 'Regional Vaccine Store', 'Regional Vaccine Store', 3, 6, 3.00, 17, TRUE)
    ,('dvs', 'District Vaccine Store', 'District Vaccine Store', 4, 6, 3.00, 18, TRUE)
;

INSERT INTO facilities (code, name, geographiczoneid, typeid, sdp, active, golivedate, enabled, virtualfacility)
VALUES ('CVS00001','Tanzania CVS',(SELECT id FROM geographic_zones WHERE name = 'Karatu'),(SELECT id FROM facility_types WHERE code = 'cvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
    ,('RVS00001','Arusha RVS',(SELECT id FROM geographic_zones WHERE name = 'Karatu'),(SELECT id FROM facility_types WHERE code = 'rvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
    ,('RVS00011','Lindi RVS',(SELECT id FROM geographic_zones WHERE name = 'Kilwa'),(SELECT id FROM facility_types WHERE code = 'rvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
    ,('DVS00003','Karatu DVS',(SELECT id FROM geographic_zones WHERE name = 'Karatu'),(SELECT id FROM facility_types WHERE code = 'dvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
    ,('DVS00004','Longido DVS',(SELECT id FROM geographic_zones WHERE name = 'Longido'),(SELECT id FROM facility_types WHERE code = 'dvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
    ,('DVS00061','Kilwa DVS',(SELECT id FROM geographic_zones WHERE name = 'Kilwa'),(SELECT id FROM facility_types WHERE code = 'dvs'),FALSE,TRUE,'2015-05-01 00:00:00',TRUE,FALSE)
;

INSERT INTO equipment_inventories (facilityid, programid, equipmentid, yearofinstallation)
VALUES ((SELECT id FROM facilities WHERE name = 'Arusha RVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM equipments WHERE name = 'WICR / WICR (30 Cubic Meters)'),2015)
;

INSERT INTO equipment_inventory_statuses (inventoryid, statusid)
VALUES ((SELECT ei.id
         FROM equipment_inventories ei
           JOIN facilities f ON ei.facilityid = f.id
           JOIN programs p ON ei.programid = p.id
           JOIN equipments e ON ei.equipmentid = e.id
         WHERE f.name = 'Arusha RVS'
           AND p.code = 'Vaccine'
           AND e.name = 'WICR / WICR (30 Cubic Meters)'
        ),(SELECT id FROM equipment_operational_status WHERE name = 'Functional'))
;

INSERT INTO programs_supported (facilityid,programid,startdate,active)
VALUES ((SELECT id FROM facilities WHERE name = 'Aya-Labe'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Basodowishi'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Buger'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Eluwai'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Chumo'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Darajani'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Tanzania CVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Arusha RVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Lindi RVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Karatu DVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Longido DVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Kilwa DVS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
;

INSERT INTO supervisory_nodes (parentid, facilityid, name, code)
VALUES (NULL,(SELECT id FROM facilities WHERE name = 'Tanzania CVS'),'Tanzania CVS','CVS')
;

INSERT INTO supervisory_nodes (parentid, facilityid, name, code)
VALUES ((SELECT id FROM supervisory_nodes WHERE name = 'Tanzania CVS'),(SELECT id FROM facilities WHERE name = 'Arusha RVS'),'Arusha RVS','ARS-RVS')
    ,((SELECT id FROM supervisory_nodes WHERE name = 'Tanzania CVS'),(SELECT id FROM facilities WHERE name = 'Lindi RVS'),'Lindi RVS','LIN-RVS')
;

INSERT INTO supervisory_nodes (parentid, facilityid, name, code)
VALUES ((SELECT id FROM supervisory_nodes WHERE name = 'Arusha RVS'),(SELECT id FROM facilities WHERE name = 'Karatu DVS'),'Karatu DVS','KAR-DVS')
    ,((SELECT id FROM supervisory_nodes WHERE name = 'Arusha RVS'),(SELECT id FROM facilities WHERE name = 'Longido DVS'),'Longido DVS','LON-DVS')
    ,((SELECT id FROM supervisory_nodes WHERE name = 'Lindi RVS'),(SELECT id FROM facilities WHERE name = 'Kilwa DVS'),'Kilwa DVS','KIL-DVS')
;

INSERT INTO requisition_groups (code,name,supervisorynodeid)
VALUES ('ARS-KAR-V','Arusha Karatu Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Karatu DVS'))
    ,('ARS-LON-V','Arusha Longido Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Longido DVS'))
    ,('LIN-KIL-V','Lindi Kilwa Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Kilwa DVS'))
    ,('ARS-V','Arusha Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Arusha RVS'))
    ,('LIN-V','Lindi Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Lindi RVS'))
    ,('TANZ-V','Tanzania Vaccine',(SELECT id FROM supervisory_nodes WHERE name = 'Tanzania CVS'))
;

INSERT INTO requisition_group_members (requisitiongroupid,facilityid)
VALUES ((SELECT id FROM requisition_groups WHERE code = 'ARS-KAR-V'),(SELECT id FROM facilities WHERE name = 'Basodowishi'))
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-KAR-V'),(SELECT id FROM facilities WHERE name = 'Buger'))
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-LON-V'),(SELECT id FROM facilities WHERE name = 'Eluwai'))
    ,((SELECT id FROM requisition_groups WHERE code = 'LIN-KIL-V'),(SELECT id FROM facilities WHERE name = 'Chumo'))
    ,((SELECT id FROM requisition_groups WHERE code = 'LIN-KIL-V'),(SELECT id FROM facilities WHERE name = 'Darajani'))
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-V'),(SELECT id FROM facilities WHERE name = 'Karatu DVS'))
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-V'),(SELECT id FROM facilities WHERE name = 'Longido DVS'))
    ,((SELECT id FROM requisition_groups WHERE code = 'LIN-V'),(SELECT id FROM facilities WHERE name = 'Kilwa DVS'))
    ,((SELECT id FROM requisition_groups WHERE code = 'TANZ-V'),(SELECT id FROM facilities WHERE name = 'Arusha RVS'))
    ,((SELECT id FROM requisition_groups WHERE code = 'TANZ-V'),(SELECT id FROM facilities WHERE name = 'Lindi RVS'))
;

INSERT INTO requisition_group_program_schedules (requisitiongroupid,programid,scheduleid,directdelivery)
VALUES ((SELECT id FROM requisition_groups WHERE code = 'ARS-KAR-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-LON-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'LIN-KIL-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'ARS-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'LIN-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'TANZ-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
;

INSERT INTO roles (name)
VALUES ('Inventory Manager')
;

INSERT INTO role_rights (roleid, rightname)
VALUES ((SELECT id FROM roles WHERE name = 'Inventory Manager'),'MANAGE_EQUIPMENT_INVENTORY')
    ,((SELECT id FROM roles WHERE name = 'All Reports'),'VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT')
    ,((SELECT id FROM roles WHERE name = 'All Reports'),'VIEW_CCE_STORAGE_CAPACITY_REPORT')
    ,((SELECT id FROM roles WHERE name = 'Equipment Reports'),'VIEW_COLD_CHAIN_EQUIPMENT_LIST_REPORT')
    ,((SELECT id FROM roles WHERE name = 'Equipment Reports'),'VIEW_CCE_STORAGE_CAPACITY_REPORT')
    ,((SELECT id FROM roles WHERE name = 'All Reports'),'VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY')
    ,((SELECT id FROM roles WHERE name = 'Equipment Reports'),'VIEW_VACCINE_REPLACEMENT_PLAN_SUMMARY')
    ,((SELECT id FROM roles WHERE name = 'All Reports'),'VIEW_REPAIR_MANAGEMENT_REPORT')
    ,((SELECT id FROM roles WHERE name = 'Equipment Reports'),'VIEW_REPAIR_MANAGEMENT_REPORT')
;

UPDATE users
SET facilityid = (SELECT id FROM facilities WHERE name = 'Tanzania CVS')
WHERE username = 'vims-admin'
;

INSERT INTO users (username, password, firstname, lastname, email, facilityid, verified, active, restrictlogin)
VALUES ('vims-rivo','gFwKyz3YODUIQPm13T1dTGytmiigV6QiiUoeZHbbcisIe3kKVipLdGW1JwTF3YisyisWipp5dCm2t66Y1Y0hSOiip9V1aAieie','Vims','RIVO','test@test5.com',(SELECT id FROM facilities WHERE name = 'Arusha RVS'),TRUE,TRUE,FALSE)
    ,('vims-divo','gFwKyz3YODUIQPm13T1dTGytmiigV6QiiUoeZHbbcisIe3kKVipLdGW1JwTF3YisyisWipp5dCm2t66Y1Y0hSOiip9V1aAieie','Vims','DIVO','test@test6.com',(SELECT id FROM facilities WHERE name = 'Karatu DVS'),TRUE,TRUE,FALSE)
;

INSERT INTO role_assignments (userid,roleid,programid,supervisorynodeid)
VALUES ((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'ils'),NULL)
    ,((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),NULL)
    ,((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM supervisory_nodes WHERE name = 'Tanzania CVS'))
    ,((SELECT id FROM users where username = 'vims-rivo'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),NULL)
    ,((SELECT id FROM users where username = 'vims-rivo'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM supervisory_nodes WHERE name = 'Arusha RVS'))
    ,((SELECT id FROM users where username = 'vims-divo'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),NULL)
    ,((SELECT id FROM users where username = 'vims-divo'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM supervisory_nodes WHERE name = 'Karatu DVS'))
    ,((SELECT id FROM users where username = 'vims-rivo'),(SELECT id FROM roles WHERE name = 'Equipment Reports'),NULL,NULL)
    ,((SELECT id FROM users where username = 'vims-divo'),(SELECT id FROM roles WHERE name = 'Equipment Reports'),NULL,NULL)
;

END;
$$