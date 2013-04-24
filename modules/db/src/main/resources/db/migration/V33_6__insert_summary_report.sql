delete from rights where name = 'VIEW_SUMMARY_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_SUMMARY_REPORT',TRUE,'Permission to view Summary Report')