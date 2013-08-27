INSERT INTO shipment_configuration VALUES ( FALSE, 'dd/MM/yy','dd/MM/yy');

INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.order.number', 1, TRUE, TRUE);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.product.code', 2, TRUE, TRUE);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.quantity.shipped', 3, TRUE, TRUE);
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.cost', 4, FALSE, FALSE );
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.packed.date', 5, FALSE , FALSE );
INSERT INTO shipment_file_columns (dataFieldLabel, position, includedInShipmentFile, mandatory ) VALUES ('header.ship.date', 6, FALSE , FALSE );
