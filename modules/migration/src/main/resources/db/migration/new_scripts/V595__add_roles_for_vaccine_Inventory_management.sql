DELETE FROM rights where name = 'VIEW_STOCK_ON_HAND';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('VIEW_STOCK_ON_HAND','REQUISITION','right.view.stock.on.hand','Permission to view stock on hand');


DELETE FROM rights WHERE name = 'MANAGE_VACCINE_PRODUCTS_CONFIGURATION';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('MANAGE_VACCINE_PRODUCTS_CONFIGURATION','ADMIN','right.manage.vaccine.product.configuration','Permission to manage vaccine product configuration');


DELETE FROM rights WHERE name = 'ADJUST_STOCK';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('ADJUST_STOCK','REQUISITION','right.adjust.stock','Permission to adjust stock');

DELETE FROM rights WHERE name = 'RECEIVE_STOCK';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('RECEIVE_STOCK','REQUISITION','right.receive.stock','Permission to do receive stock');