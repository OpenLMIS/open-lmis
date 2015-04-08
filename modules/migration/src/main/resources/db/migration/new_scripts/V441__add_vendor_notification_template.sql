DELETE FROM configuration_settings where key = 'VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('VENDOR_MAINTENANCE_REQUEST_EMAIL_TEMPLATE', 'Pending maintenance notification template','Notification - Email','This template is used when sending Email notification when there is pending maintenance request','Hi {vendor}, Please note that there is a pending maintenance request for your organization. Thank you.', 'HTML', 40);

ALTER TABLE configuration_settings ALTER COLUMN value TYPE varchar(2000);