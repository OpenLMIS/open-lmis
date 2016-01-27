update configuration_settings set name = 'Country Name' where key = 'COUNTRY';
update configuration_settings set name = 'Main Logo File Name' where key = 'LOGO_FILE_NAME';
update configuration_settings set name = 'Operator Logo File Name' where key = 'OPERATOR_LOGO_FILE_NAME';
update configuration_settings set name = 'eLMIS Start Year' where key = 'START_YEAR';
update configuration_settings set name = 'Months' where key = 'MONTHS';
update configuration_settings set name = 'Reports (No Data Messages)' where key = 'REPORT_MESSAGE_WHEN_NO_DATA';
update configuration_settings set name = 'Enable email notifications' where key = 'ENABLE_NOTIFICATIONS';

insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('EMAIL_TEMPLATE_APPROVAL', 'Requisition Approval Notification email template','Email Notificaiton','This template is used when sending email notification to approvers.<br />Please use the following as place holders. {approver_name}, {facility_name}, {period}','Dear {approver_name} This is to inform you that {facility_name} has completed its Report and Requisition for the Period {period} and requires your approval. Please login to approve / reject it. Thank you.', 'TEXT_AREA', 10);

insert into configuration_settings (key, name, groupname, description, value, valueType, displayOrder)
	values ('EMAIL_SUBJECT_APPROVAL', 'Requisition Approval Notification Subject','Email Notificaiton', null ,'Your Action is Required', 'TEXT',9);