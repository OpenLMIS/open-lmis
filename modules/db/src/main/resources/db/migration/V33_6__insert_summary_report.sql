delete from rights where name = 'VIEW_SUMMARY_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_SUMMARY_REPORT','REPORT','Permission to view Summary Report')