DELETE FROM rights where name = 'VIEW_ORDER_REQUISITION';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('VIEW_ORDER_REQUISITION','REQUISITION','right.view.order.requisition','Permission to view Order Requisition');


DELETE FROM rights WHERE name = 'VIEW_PENDING_REQUEST';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('VIEW_PENDING_REQUEST','REQUISITION','right.view.pending.request','Permission to View Pending Request');

DELETE FROM rights WHERE name = 'CREATE_ORDER_REQUISITION';

INSERT INTO rights (name, rightType, displaynamekey, description) VALUES
 ('CREATE_ORDER_REQUISITION','REQUISITION','right.create.order.requisition','Permission to Create Requisition');