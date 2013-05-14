delete from rights where name = 'VIEW_STOCKED_OUT_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_STOCKED_OUT_REPORT',TRUE,'Permission to view stocked out commodity Report');