delete from rights where name = 'VIEW_NON_REPORTING_FACILITIES';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_NON_REPORTING_FACILITIES','REPORT','Permission to view Non reporting Facility List Report');
