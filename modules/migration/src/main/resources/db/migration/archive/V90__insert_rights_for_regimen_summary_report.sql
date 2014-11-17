delete from rights where name = 'VIEW_REGIMEN_SUMMARY_REPORT';

INSERT INTO rights(name, righttype, description) VALUES
 ('VIEW_REGIMEN_SUMMARY_REPORT','REPORT','Permission to view Regimen Summary Report.');