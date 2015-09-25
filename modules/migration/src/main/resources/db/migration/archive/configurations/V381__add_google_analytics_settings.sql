delete from configuration_settings where key = 'ENABLE_GOOGLE_ANALYTICS';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('ENABLE_GOOGLE_ANALYTICS', 'Enable Google Analytics', 'Analytics', '','false',  'BOOLEAN', 1);


delete from configuration_settings where key = 'GOOGLE_ANALYTICS_TRACKING_CODE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('GOOGLE_ANALYTICS_TRACKING_CODE', 'Google Analytics Tracking Code', 'Analytics', '','UA-49644602-3',  'TEXT', 2);

