delete from rights where name = 'VIEW_DISTRICT_CONSUMPTION_REPORT';

INSERT INTO rights(name, righttype,description) VALUES
 ('VIEW_DISTRICT_CONSUMPTION_REPORT','ADMIN','Permission to view district consumption comparison report');