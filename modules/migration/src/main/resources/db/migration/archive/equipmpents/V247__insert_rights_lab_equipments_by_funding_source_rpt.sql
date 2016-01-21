delete from rights where name = 'VIEW_LAB_EQUIPMENTS_BY_FUNDING_SOURCE';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_LAB_EQUIPMENTS_BY_FUNDING_SOURCE','REPORT','Permission to view lab equipment list by funding source Report');