INSERT INTO order_configurations VALUES ('O', FALSE, 'dd/MM/yy', 'MM/yy');

INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('header.order.number', 'order', 'id', 'Order number', 1, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('create.facility.code', 'order', 'rnr/facility/code', 'Facility code', 2, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('header.product.code', 'lineItem', 'productCode', 'Product code', 3, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('header.quantity.approved', 'lineItem', 'quantityApproved', 'Approved quantity', 4, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('label.period', 'order', 'rnr/period/startDate', 'Period', 5, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, position, openLmisField) VALUES ('header.order.date', 'order', 'createdDate', 'Order date', 6, TRUE);
