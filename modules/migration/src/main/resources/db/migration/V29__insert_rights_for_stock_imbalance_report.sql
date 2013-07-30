delete from rights where name = 'VIEW_STOCK_IMBALANCE_REPORT';

INSERT INTO rights(name, righttype, description) VALUES
 ('VIEW_STOCK_IMBALANCE_REPORT','ADMIN','Permission to view Stock Imbalance Report.');