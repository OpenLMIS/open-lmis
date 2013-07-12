delete from rights where name = 'VIEW_SUPPLY_STATUS_REPORT';

INSERT INTO rights(name, righttype,description) VALUES
 ('VIEW_SUPPLY_STATUS_REPORT','REPORT','Permission to view supply status by facility report');