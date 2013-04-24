delete from rights where name = 'VIEW_FACILITY_REPORT';
delete from rights where name = 'VIEW_MAILING_LABEL_REPORT';
delete from rights where name = 'VIEW_CONSUMPTION_REPORT';
delete from rights where name = 'VIEW_REPORTING_RATE_REPORT';

INSERT INTO rights(name, adminRight,description) VALUES
 ('VIEW_FACILITY_REPORT',TRUE,'Permission to view Facility List Report'),
 ('VIEW_MAILING_LABEL_REPORT',TRUE,'Permission to view Mailing labels for Facilities'),
 ('VIEW_CONSUMPTION_REPORT',TRUE,'Permission to view Consumption Report'),
 ('VIEW_REPORTING_RATE_REPORT',TRUE,'Permission to view Reporting Rate Report');




