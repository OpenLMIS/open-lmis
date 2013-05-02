delete from rights where name = 'VIEW_AVERAGE_CONSUMPTION_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_AVERAGE_CONSUMPTION_REPORT',TRUE,'Permission to view avergae consumption Report');