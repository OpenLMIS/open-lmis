
--Product Recalled
DELETE FROM configuration_settings where key = 'PRODUCT_RECALLED_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('PRODUCT_RECALLED_SMS_MESSAGE_TEMPLATE', 'Product Recalled Notification SMS template','Notificaiton','This template is used when sending SMS notification when a product is recalled','Please note that the following products have been recalled. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'PRODUCT_RECALLED_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('PRODUCT_RECALLED_EMAIL_MESSAGE_TEMPLATE', 'Product Recalled Notification Email template','Notificaiton','This template is used when sending Email notification when a product is recalled','Hi,  Please note that the following products have been recalled. Thank you.', 'TEXT_AREA', 31);

update alerts set sms_msg_template_key = 'PRODUCT_RECALLED_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'PRODUCT_RECALLED_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'PRODUCT_RECALLED';

--Requisition Pending
DELETE FROM configuration_settings where key = 'REQUISITION_PENDING_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('REQUISITION_PENDING_SMS_MESSAGE_TEMPLATE', 'Requisition Pending Notification SMS template','Notificaiton','This template is used when sending SMS notification when there is pending requisition','Please note that your rnr for period {quarter_name, year} is being processed. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'REQUISITION_PENDING_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('REQUISITION_PENDING_EMAIL_MESSAGE_TEMPLATE', 'Requisition Pending Notification Email template','Notificaiton','This template is used when sending Email notification when there is pending requisition','Hi, Please note that your rnr for period {quarter_name, year} is being processed. Thank you.', 'TEXT_AREA', 31);
	
update alerts set sms_msg_template_key = 'REQUISITION_PENDING_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'REQUISITION_PENDING_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'REQUISITION_PENDING';

--Requisition Rejected
DELETE FROM configuration_settings where key = 'REQUISITION_REJECTED_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('REQUISITION_REJECTED_SMS_MESSAGE_TEMPLATE', 'Requisition Rejected Notification SMS template','Notificaiton','This template is used when sending SMS notification when there is rejected requisition','Please note that your rnr for period {quarter_name, year} not approved. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'REQUISITION_REJECTED_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('REQUISITION_REJECTED_EMAIL_MESSAGE_TEMPLATE', 'Requisition Rejected Notification Email template','Notificaiton','This template is used when sending SMS notification when there is rejected requisition','Hi, Please note that your rnr for period {quarter_name, year} not approved. Thank you.', 'TEXT_AREA', 31);

update alerts set sms_msg_template_key = 'REQUISITION_REJECTED_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'REQUISITION_REJECTED_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'REQUISITION_REJECTED';

--Submit RnR Reminder
DELETE FROM configuration_settings where key = 'SUBMIT_RNR_REMINDER_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('SUBMIT_RNR_REMINDER_SMS_MESSAGE_TEMPLATE', 'Submit RnR Reminder Notification SMS template','Notificaiton','This template is used when sending SMS notification of RnR due date ','This a friendly reminder that your RnR for period { quarter_name, year} is due on {due_date}. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'SUBMIT_RNR_REMINDER_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('SUBMIT_RNR_REMINDER_EMAIL_MESSAGE_TEMPLATE', 'Submit RnR Reminder Notification Email template','Notificaiton','This template is used when sending Email notification of RnR due date ','Hi, This a friendly reminder that your RnR for period { quarter_name, year} is due on {due_date}. Thank you.', 'TEXT_AREA', 31);

update alerts set sms_msg_template_key = 'SUBMIT_RNR_REMINDER_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'SUBMIT_RNR_REMINDER_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'SUBMIT_RNR_REMINDER';

--Stock Status
DELETE FROM configuration_settings where key = 'STOCK_STATUS_SMS_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('STOCK_STATUS_SMS_MESSAGE_TEMPLATE', 'Stocked out Notification SMS template','Notificaiton','This template is used when sending SMS notification of stocked out product ','Please note that the following products are currently stocked out. Thank you.', 'TEXT_AREA', 31);

DELETE FROM configuration_settings where key = 'STOCK_STATUS_EMAIL_MESSAGE_TEMPLATE';
insert into configuration_settings (key, name, groupname, description, value, valueType,displayOrder)
	values ('STOCK_STATUS_EMAIL_MESSAGE_TEMPLATE', 'Stocked out Notification Email template','Notificaiton','This template is used when sending Email notification of stocked out product ','Hi, Please note that the following products are currently stocked out. Thank you.', 'TEXT_AREA', 31);

update alerts set sms_msg_template_key = 'STOCK_STATUS_SMS_MESSAGE_TEMPLATE', email_msg_template_key = 'STOCK_STATUS_EMAIL_MESSAGE_TEMPLATE' where alerttype = 'STOCK_STATUS';