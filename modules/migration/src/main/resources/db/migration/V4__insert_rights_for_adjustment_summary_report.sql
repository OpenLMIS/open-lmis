delete from rights where name = 'VIEW_ADJUSTMENT_SUMMARY_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_ADJUSTMENT_SUMMARY_REPORT',TRUE,'Permission to view adjustment summary Report');