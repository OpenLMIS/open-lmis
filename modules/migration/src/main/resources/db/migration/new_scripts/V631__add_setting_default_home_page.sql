delete from configuration_settings where key = 'LOGIN_SUCCESS_DEFAULT_LANDING_PAGE';

INSERT INTO configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
  values ('LOGIN_SUCCESS_DEFAULT_LANDING_PAGE', 'Configure Default Home Page', 'Dashboard', '','/public/pages/dashboard/index.html#/dashboard',  'TEXT', 1);

