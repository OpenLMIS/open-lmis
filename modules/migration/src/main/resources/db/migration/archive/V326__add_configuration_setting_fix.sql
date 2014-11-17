delete from configuration_settings where key = 'OPERATOR_NAME';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
  values ('OPERATOR_NAME', 'Reporting header main title: Name of the organization', 'GENERAL', '','Ministry of Health and Social Welfare',  'TEXT', 1);
