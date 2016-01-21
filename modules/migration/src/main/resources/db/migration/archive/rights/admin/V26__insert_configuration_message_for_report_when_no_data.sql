delete from configuration_settings where key = 'REPORT_MESSAGE_WHEN_NO_DATA';
INSERT INTO configuration_settings (key, value)
   VALUES ('REPORT_MESSAGE_WHEN_NO_DATA', 'no data found');

