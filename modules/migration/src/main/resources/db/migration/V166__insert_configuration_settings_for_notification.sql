INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('SMS_ENABLED', 'Is SMS Enabled','Notification','',E'false', 'BOOLEAN', 29);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('LATE_RNR_NOTIFICATION_EMAIL_TEMPLATE', 'Late RnR Email Notification Template','Notification','',E'Dear {name} \n\n{facility_name} facility has not reported for {period}. Please submit RnR for this period. \n\nThank you', 'TEXT_AREA', 30);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('LATE_RNR_NOTIFICATION_SMS_TEMPLATE', 'Late RnR Email Notification Template','Notification','',E'Dear {name}, Please submit RnR for this period {facility_name}', 'TEXT_AREA', 31);