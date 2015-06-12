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
;

INSERT INTO equipment_cold_chain_equipments (equipmentid, designationid, refrigeratorcapacity, freezercapacity, pqsstatusid)
VALUES ((SELECT id FROM equipments WHERE name = 'Dometic / TCW 2000 SDD'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),99.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Dometic / TCW 3000 SDD'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),156.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Haier / HBD-286'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Freezer'),NULL,224.00,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Sibir / V170 EK'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator & Freezer'),55.00,36.00,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Not Recommended'))
    ,((SELECT id FROM equipments WHERE name = 'Vestfrost / VLS 400'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),216.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
    ,((SELECT id FROM equipments WHERE name = 'Vestfrost / VLS 350'),(SELECT id FROM equipment_cold_chain_equipment_designations WHERE name = 'Refrigerator'),196.00,NULL,(SELECT id FROM equipment_cold_chain_equipment_pqs_status WHERE name = 'Approved'))
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

INSERT INTO programs_supported (facilityid,programid,startdate,active)
VALUES ((SELECT id FROM facilities WHERE name = 'Aya-Labe'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Arash'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE code = 'MT'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE code = 'MS'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Basodowishi'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Buger'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Eluwai'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Chumo'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
    ,((SELECT id FROM facilities WHERE name = 'Darajani'),(SELECT id FROM programs WHERE code = 'Vaccine'),'2015-05-01 00:00:00',TRUE)
;

INSERT INTO requisition_groups (code,name,supervisorynodeid)
VALUES ('CSA-MOSH-KAR-V','Moshi Karatu Vaccine',(SELECT id FROM supervisory_nodes WHERE code = 'MSH-SND3'))
    ,('CSA-MOSH-LON-V','Moshi Longido Vaccine',(SELECT id FROM supervisory_nodes WHERE code = 'MSH-SND4'))
    ,('CSA-LIND-KIL-V','Lindi Kilwa Vaccine',(SELECT id FROM supervisory_nodes WHERE code = 'MTWA-SND1'))
;

INSERT INTO requisition_group_members (requisitiongroupid,facilityid)
VALUES ((SELECT id FROM requisition_groups WHERE code = 'CSA-MOSH-KAR-V'),(SELECT id FROM facilities WHERE name = 'Basodowishi'))
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-MOSH-KAR-V'),(SELECT id FROM facilities WHERE name = 'Buger'))
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-MOSH-LON-V'),(SELECT id FROM facilities WHERE name = 'Eluwai'))
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-LIND-KIL-V'),(SELECT id FROM facilities WHERE name = 'Chumo'))
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-LIND-KIL-V'),(SELECT id FROM facilities WHERE name = 'Darajani'))
;

INSERT INTO requisition_group_program_schedules (requisitiongroupid,programid,scheduleid,directdelivery)
VALUES ((SELECT id FROM requisition_groups WHERE code = 'CSA-MOSH-KAR-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-MOSH-LON-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
    ,((SELECT id FROM requisition_groups WHERE code = 'CSA-LIND-KIL-V'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM processing_schedules WHERE code = 'Monthly'),TRUE)
;

INSERT INTO roles (name)
VALUES ('Inventory Manager')
;

INSERT INTO role_rights (roleid, rightname)
VALUES ((SELECT id FROM roles WHERE name = 'Inventory Manager'),'MANAGE_EQUIPMENT_INVENTORY')
;

INSERT INTO role_assignments (userid,roleid,programid,supervisorynodeid)
VALUES ((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),NULL)
    ,((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'ils'),NULL)
    ,((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM supervisory_nodes WHERE code = 'MSH-SNZ'))
    ,((SELECT id FROM users where username = 'vims-admin'),(SELECT id FROM roles WHERE name = 'Inventory Manager'),(SELECT id FROM programs WHERE code = 'Vaccine'),(SELECT id FROM supervisory_nodes WHERE code = 'MTWA-SNZ'))
;

UPDATE users
SET facilityid = (SELECT id FROM facilities WHERE name = 'Aya-Labe')
WHERE username = 'vims-admin'
;

END;
$$