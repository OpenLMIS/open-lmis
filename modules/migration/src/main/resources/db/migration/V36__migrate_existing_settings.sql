update configurations set name = 'Country Name' where key = 'COUNTRY';
update configurations set name = 'Main Logo File Name' where key = 'LOGO_FILE_NAME';
update configurations set name = 'Operator Logo File Name' where key = 'OPERATOR_LOGO_FILE_NAME';
update configurations set name = 'eLMIS Start Year' where key = 'START_YEAR';
update configurations set name = 'Months' where key = 'MONTHS';
update configurations set name = 'Reports (No Data Messages)' where key = 'REPORT_MESSAGE_WHEN_NO_DATA';
update configurations set name = 'Enable email notifications' where key = 'ENABLE_NOTIFICATIONS';
insert into configurations (key, name, description, value, valueType)
	values ('EMAIL_TEMPLATE', 'Notification email template','Please use the following as place holders. {approver_name}, {facility_name}, {period}, {link}','-', 'TEXT_AREA');