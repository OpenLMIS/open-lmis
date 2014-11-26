delete from configuration_settings where key = 'LATE_RNR_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('LATE_RNR_SUPERVISOR_NOTIFICATION_EMAIL_TEMPLATE',
  'Late RnR Email for supervisor Notification Template','Notification','',
  E'Dear {name} \n\nPlease find attached list of facilities that did not report in your district.',
   'TEXT_AREA', 31);