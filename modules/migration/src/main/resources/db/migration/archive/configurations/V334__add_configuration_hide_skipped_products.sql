delete from configuration_settings where key = 'RNR_HIDE_SKIPPED_PRODUCTS';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('RNR_HIDE_SKIPPED_PRODUCTS', 'Hide Skipped Products', 'R & R Options', '','true',  'BOOLEAN', 50);

delete from configuration_settings where key = 'RNR_HIDE_NON_FULL_SUPPLY_TAB';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('RNR_HIDE_NON_FULL_SUPPLY_TAB', 'Hide Non Full Supply Tab', 'R & R Options', '','false',  'BOOLEAN', 51);