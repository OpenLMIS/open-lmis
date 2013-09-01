INSERT INTO shipment_configuration VALUES (FALSE);

INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.order.number', 1, TRUE, TRUE, null);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.product.code', 2, TRUE, TRUE, null);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.quantity.shipped', 3, TRUE, TRUE, null);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.cost', 4, FALSE, FALSE, null);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.packed.date', 5, FALSE , FALSE, 'dd/MM/yy');
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ,datePattern) VALUES ('header.shipped.date', 6, FALSE , FALSE, 'dd/MM/yy');
