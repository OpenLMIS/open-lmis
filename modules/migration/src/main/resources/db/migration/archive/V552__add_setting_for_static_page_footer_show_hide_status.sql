delete from configuration_settings where key = 'STATIC_PAGE_FOOTER_STATUS';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('STATIC_PAGE_FOOTER_STATUS', 'Static Page Footer Enabled', 'GENERAL', '','true',  'BOOLEAN', 59);
