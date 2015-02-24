delete from configuration_settings where key = 'TRACK_VACCINE_CAMPAIGN_COVERAGE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('TRACK_VACCINE_CAMPAIGN_COVERAGE', 'Track Campaign On Vaccine Coverage', 'Vaccine', '','true',  'BOOLEAN', 100);
