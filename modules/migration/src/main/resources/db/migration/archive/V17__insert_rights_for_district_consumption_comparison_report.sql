delete from rights where name = 'VIEW_DISTRICT_CONSUMPTION_REPORT';

INSERT INTO rights(name, rightType,description) VALUES
 ('VIEW_DISTRICT_CONSUMPTION_REPORT','REPORT','Permission to view district consumption comparison report');