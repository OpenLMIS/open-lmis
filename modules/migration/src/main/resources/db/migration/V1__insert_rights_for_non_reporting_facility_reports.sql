delete from rights where name = 'VIEW_NON_REPORTING_FACILITIES';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_NON_REPORTING_FACILITIES',TRUE,'Permission to view Non reporting Facility List Report');
