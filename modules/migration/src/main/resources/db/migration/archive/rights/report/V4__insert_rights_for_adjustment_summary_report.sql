delete from rights where name = 'VIEW_ADJUSTMENT_SUMMARY_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_ADJUSTMENT_SUMMARY_REPORT','REPORT','Permission to view adjustment summary Report');