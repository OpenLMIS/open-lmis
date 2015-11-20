INSERT INTO rights (name, rightType, description, displaynamekey) VALUES
('VIEW_REQUISITION_REPORT','REPORT','Permission to View Requisitions Report', 'right.report.requisition');

INSERT INTO rights (name, righttype, description, displaynamekey) VALUES
('VIEW_STOCK_ON_HAND_REPORT', 'REPORT', 'Permission to View Stock On Hand Report', 'right.report.stockonhand');

UPDATE rights SET displayOrder = 27, displayNameKey = 'right.manage.products' WHERE name = 'MANAGE_PRODUCT';
UPDATE rights SET displayOrder = 24, displayNameKey = 'right.manage.requisition.group' WHERE name = 'MANAGE_REQUISITION_GROUP';
UPDATE rights SET displayOrder = 8, displayNameKey= 'right.manage.supervisory.node' WHERE name = 'MANAGE_SUPERVISORY_NODE';
