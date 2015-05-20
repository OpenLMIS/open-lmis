UPDATE configuration_settings
  SET isConfigurable = FALSE
  WHERE groupname = 'Vaccine';


delete from configuration_settings where key = 'TRACK_VACCINE_OUTREACH_COVERAGE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('TRACK_VACCINE_OUTREACH_COVERAGE', 'Track Outreach On Vaccine Coverage', 'Vaccine', '','false',  'BOOLEAN', 90, false);




INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_LOGISTICS_VISIBLE', 'Show Logistics Tab', 'Vaccine', '','true',  'BOOLEAN', 101, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_COVERAGE_VISIBLE', 'Show Coverage Tab', 'Vaccine', '','true',  'BOOLEAN', 102, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_DISEASE_VISIBLE', 'Show Disease Tab', 'Vaccine', '','true',  'BOOLEAN', 103, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_AEFI_VISIBLE', 'Show AEFI Tab', 'Vaccine', '','true',  'BOOLEAN', 104, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_TARGET_VISIBLE', 'Show Target Tab', 'Vaccine', '','true',  'BOOLEAN', 105, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_COLD_CHAIN_VISIBLE', 'Show Cold Chain Tab', 'Vaccine', '','true',  'BOOLEAN', 106, false);

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder, isConfigurable)
  values ('VACCINE_TAB_CAMPAIGN_VISIBLE', 'Show Campaign Tab', 'Vaccine', '','true',  'BOOLEAN', 107, false);