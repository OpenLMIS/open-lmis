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

INSERT INTO equipment_types (id, code, name, iscoldchain)
VALUES (36, 'CCE1', 'Cold Chain Equipment', TRUE)
;

INSERT INTO equipments (name, equipmenttypeid, manufacturer, model, energytypeid)
VALUES ('Dometic / TCW 2000 SDD',36,'Dometic','TCW 2000 SDD',3)
    ,('Dometic / TCW 3000 SDD',36,'Dometic','TCW 3000 SDD',3)
    ,('Haier / HBD-286',36,'Haier','HBD-286',1)
    ,('Sibir / V170 EK',36,'Sibir','V170 EK',4)
    ,('Vestfrost / VLS 400',36,'Vestfrost','VLS 400',1)
    ,('Vestfrost / VLS 350',36,'Vestfrost','VLS 350',1)
;

INSERT INTO equipment_cold_chain_equipments (equipmentid, designationid, refrigeratorcapacity, freezercapacity, pqsstatusid)
VALUES (41,2,99.00,NULL,1)
    ,(42,2,156.00,NULL,1)
    ,(43,3,NULL,224.00,1)
    ,(44,1,55.00,36.00,2)
    ,(45,2,216.00,NULL,1)
    ,(46,2,196.00,NULL,1)
;

INSERT INTO equipment_type_programs (programid, equipmenttypeid, displayorder)
VALUES (82,36,0)
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
WHERE id = 82
;

INSERT INTO programs_supported (facilityid,programid,startdate,active)
VALUES (16403,82,'2015-05-01 00:00:00',TRUE)
    ,(16399,82,'2015-05-01 00:00:00',TRUE)
    ,(14054,82,'2015-05-01 00:00:00',TRUE)
    ,(14058,82,'2015-05-01 00:00:00',TRUE)
    ,(16409,82,'2015-05-01 00:00:00',TRUE)
    ,(16416,82,'2015-05-01 00:00:00',TRUE)
    ,(16440,82,'2015-05-01 00:00:00',TRUE)
    ,(16887,82,'2015-05-01 00:00:00',TRUE)
    ,(16889,82,'2015-05-01 00:00:00',TRUE)
;

INSERT INTO requisition_groups (code,name,supervisorynodeid)
VALUES ('CSA-MOSH-KAR-V','Moshi Karatu Vaccine',391)
    ,('CSA-MOSH-LON-V','Moshi Longido Vaccine',390)
    ,('CSA-LIND-KIL-V','Lindi Kilwa Vaccine',333)
;

INSERT INTO requisition_group_members (requisitiongroupid,facilityid)
VALUES (2779,16409)
    ,(2779,16416)
    ,(2780,16440)
    ,(2781,16887)
    ,(2781,16889)
;

INSERT INTO requisition_group_program_schedules (requisitiongroupid,programid,scheduleid,directdelivery)
VALUES (2779,82,45,TRUE)
    ,(2780,82,45,TRUE)
    ,(2781,82,45,TRUE)
;

INSERT INTO roles (name)
VALUES ('Inventory Manager')
;

INSERT INTO role_rights (roleid, rightname)
VALUES (66,'MANAGE_EQUIPMENT_INVENTORY')
;

INSERT INTO role_assignments (userid,roleid,programid,supervisorynodeid)
VALUES (307,66,82,NULL)
    ,(307,66,1,NULL)
    ,(307,66,82,227)
    ,(307,66,82,226)
;

UPDATE users
SET facilityid = 16403
WHERE username = 'vims-admin'
;

END;
$$