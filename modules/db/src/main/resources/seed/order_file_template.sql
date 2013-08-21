INSERT INTO configurations VALUES ('O', FALSE, 'dd/MM/yy', 'MM/yy');

INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('header.order.number', 'Order number', 1, true);
INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('create.facility.code', 'Facility code', 2, true);
INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('header.product.code', 'Product code', 3, true);
INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('header.quantity.approved', 'Approved quantity', 4, true);
INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('label.period', 'Period', 5, true);
INSERT INTO order_file_columns (dataFieldLabel, columnLabel, position, openLmisField) VALUES ('header.order.date', 'Order date', 6, true);
