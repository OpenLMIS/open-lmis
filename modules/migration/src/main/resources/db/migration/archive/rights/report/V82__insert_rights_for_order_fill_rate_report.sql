delete from rights where name = 'VIEW_ORDER_FILL_RATE_REPORT';

INSERT INTO rights(name, righttype, description) VALUES
 ('VIEW_ORDER_FILL_RATE_REPORT','REPORT','Permission to view Order Fill Rate Report');