delete from configuration_settings where key = 'RNR_HIDE_SKIPPED_PRODUCTS';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('RNR_HIDE_SKIPPED_PRODUCTS', 'Hide Skipped Products', 'R & R Options', '','true',  'BOOL', 50);