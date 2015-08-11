delete from rights where name = 'MANAGE_CUSTOM_REPORTS';

INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
 ('MANAGE_CUSTOM_REPORTS','REPORT','Permission to manage custom reports', 'right.report.manage.custom.report');