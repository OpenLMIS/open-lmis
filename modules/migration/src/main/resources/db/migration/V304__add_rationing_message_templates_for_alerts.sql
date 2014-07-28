--'RATIONING' 
DELETE FROM configuration_settings where key = 'RATIONING_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('RATIONING_SMS_MESSAGE_TEMPLATE', 'Rationing Notification SMS template','Notificaiton','This template is used when sending SMS notification of rationing ','Please note that the following commodity is rationed. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'RATIONING_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('RATIONING_EMAIL_MESSAGE_TEMPLATE', 'Rationing Notification Email template','Notificaiton','This template is used when sending Email notification of rationing','Hi, Please note that the following commodity is rationed. Thank you.', 'TEXT_AREA', 31);

update alerts set sms_msg_template_key = 'RATIONING_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'RATIONING_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'RATIONING';
