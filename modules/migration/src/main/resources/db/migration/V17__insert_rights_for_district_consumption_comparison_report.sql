delete from rights where name = 'VIEW_DISTRICT_CONSUMPTION_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_DISTRICT_CONSUMPTION_REPORT',TRUE,'Permission to view district consumption comparison report');