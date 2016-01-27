insert into user_preference_master (key, name, groupName, displayOrder, description, entityType, dataType, inputType,defaultValue )
values ('DEFAULT_PROGRAM', 'Default Program', 'DEFAULTS', 1, 'Sets the default program for user (applies on dashboard)', 'program', 'int', 'single-select', '1')
  , ('DEFAULT_SCHEDULE', 'Default Schedule', 'DEFAULTS', 2, 'Sets the default schedule for user (applies on dashboard)', 'processing_schedule', 'int', 'single-select', '1')
  , ('DEFAULT_FACILITY', 'Default Facility', 'DEFAULTS', 3, 'Sets the default facility for user (applies on dashboard)', 'facility', 'int', 'single-select', '1')
  , ('DEFAULT_SUPERVISORY_NODE', 'Default Supervisory Node', 'DEFAULTS', 4, 'Sets the default facility for user (applies on dashboard)', 'supervisory_node', 'int', 'single-select', '1')
  , ('DEFAULT_REQUISITION_GROUP', 'Default Requisition Group', 'DEFAULTS', 5, 'Sets the default requisition group for user (applies on dashboard)', 'facility', 'int', 'single-select', '1')
  , ('DEFAULT_PRODUCT', 'Default Product', 'DEFAULTS', 6, 'Sets the default program for user (applies on dashboard)', 'product', 'int', 'single-select', '1')
  , ('DEFAULT_PRODUCTS', 'Default Indicator Products', 'DEFAULTS',7, 'Sets the default program for user (applies on dashboard)', 'product', 'csv', 'multi-select', '1,2,3')

  -- alert preference email examples
  , ('ALERT_EMAIL_OVER_DUE_REQUISITION', 'Email Notifications on overdue requisition', 'ALERT_EMAIL', 1, 'Send email notifications when rnr is overdue', 'none', 'boolean', 'checkbox', 'true')

  -- alert preference sms examples
  , ('ALERT_SMS_NOTIFICATION_OVERDUE_REQUISITION', 'SMS Notifications on overdue requisition', 'ALERT_SMS', 2, 'Send email notifications when rnr is overdue', 'none', 'boolean', 'checkbox', 'true')
;