INSERT INTO order_configurations VALUES ('O', FALSE, 'dd/MM/yy', 'MM/yy');

INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('header.order.number', 'id', '', 'Order number', 1, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('create.facility.code', 'code', 'rnr.facility', 'Facility code', 2, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('header.product.code', 'productCode', 'lineItem', 'Product code', 3, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('header.quantity.approved', 'quantityApproved', 'lineItem', 'Approved quantity', 4, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('label.period', 'startDate', 'rnr.period', 'Period', 5, TRUE);
INSERT INTO order_file_columns (dataFieldLabel, fieldName, nestedPath, columnLabel, position, openLmisField) VALUES ('header.order.date', 'createdDate', '', 'Order date', 6, TRUE);
