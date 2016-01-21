delete from rights where name = 'VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT';

INSERT INTO rights(name, righttype, description) VALUES
 ('VIEW_DISTRICT_FINANCIAL_SUMMARY_REPORT','REPORT','Permission to view District Financial Summary Report');