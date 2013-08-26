INSERT INTO order_configurations VALUES ('O', FALSE);

INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('header.order.number', 'order', 'id', 'Order number', '', 1, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('create.facility.code', 'order', 'rnr/facility/code', 'Facility code', '', 2, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('header.product.code', 'lineItem', 'productCode', 'Product code', '', 3, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('header.quantity.approved', 'lineItem', 'quantityApproved', 'Approved quantity', '', 4, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('label.period', 'order', 'rnr/period/startDate', 'Period', 'MM/yy', 5, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, nested, keyPath, columnLabel, format, position, openLmisField) VALUES ('header.order.date', 'order', 'createdDate', 'Order date', 'dd/MM/yy', 6, TRUE);
