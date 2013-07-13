delete from rights where name = 'VIEW_ORDER_REPORT';

INSERT INTO rights (name, rightType, description) VALUES
 ('VIEW_ORDER_REPORT','REPORT','Permission to view Order Report')