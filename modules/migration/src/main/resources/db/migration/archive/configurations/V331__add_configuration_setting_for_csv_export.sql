delete from configuration_settings where key = 'CSV_LINE_SEPARATOR';
delete from configuration_settings where key = 'CSV_APPLY_QUOTES';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('CSV_LINE_SEPARATOR', 'Line Separator', 'Order Export', '','\r\n',  'TEXT', 50);

  INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('CSV_APPLY_QUOTES', 'Apply Quotes to wrap fields', 'Order Export', 'true','Apply Quotes',  'BOOLEAN', 51);