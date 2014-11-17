delete from rights where name = 'VIEW_USER_SUMMARY_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_USER_SUMMARY_REPORT','REPORT','Permission to view user summary Report');