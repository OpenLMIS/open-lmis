delete from configuration_settings where key = 'RNR_COPY_SKIPPED_FROM_PREVIOUS_RNR';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('RNR_COPY_SKIPPED_FROM_PREVIOUS_RNR', 'Copy Skipped field from Previous R & R', 'R & R Options', '','true',  'BOOLEAN', 51);
