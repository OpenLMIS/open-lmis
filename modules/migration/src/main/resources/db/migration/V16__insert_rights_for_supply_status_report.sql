delete from rights where name = 'VIEW_SUPPLY_STATUS_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_SUPPLY_STATUS_REPORT',TRUE,'Permission to view supply status by facility report');