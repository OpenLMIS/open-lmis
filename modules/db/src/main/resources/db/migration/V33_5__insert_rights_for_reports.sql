delete from rights where name = 'VIEW_FACILITY_REPORT';
delete from rights where name = 'VIEW_MAILING_LABEL_REPORT';
delete from rights where name = 'VIEW_CONSUMPTION_REPORT';
delete from rights where name = 'VIEW_REPORTING_RATE_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_FACILITY_REPORT','REPORT','Permission to view Facility List Report'),
 ('VIEW_MAILING_LABEL_REPORT','REPORT','Permission to view Mailing labels for Facilities'),
 ('VIEW_CONSUMPTION_REPORT','REPORT','Permission to view Consumption Report'),
 ('VIEW_REPORTING_RATE_REPORT','REPORT','Permission to view Reporting Rate Report');




