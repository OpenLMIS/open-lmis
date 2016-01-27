
INSERT INTO alerts(
            alerttype, display_section, email, sms, detail_table, sms_msg_template_key, 
            email_msg_template_key)
VALUES('COMMODITY_RATIONED','ALERT',true,true,'','',''),
('EMERGENCY_REQUISITION','SUMMARY',true,true,'alert_requisition_emergency','SMS_EMERGENCY_REQUISITION','EMAIL_EMERGENCY_REQUISITION'),
('FACILITY_WITH_EMPTY_POD','SUMMARY',true,true,'','',''),
('PRODUCT_RECALLED','ALERT',true,true,'','',''),
('REQUISITION_PENDING','ALERT',true,true,'alert_requisition_pending','SMS_EMERGENCY_REQUISITION','EMAIL_REQUISITION_PENDING'),
('REQUISITION_REJECTED','ALERT',true,true,'alert_requisition_rejected','SMS_REQUISITION_REJECTED','EMAIL_REQUISITION_REJECTED'),
('UNSCHEDULED_ORDER','SUMMARY',true,true,'','',''),
('LATE_RESUPPLIED_FACILITY','SUMMARY',true,true,'','',''),
('RNR_REJECTED','',false,false,'','',''),
('SUBMIT_RNR_REMINDER','',true,false,'','SUBMIT_RNR_REMINDER_EMAIL_MESSAGE_TEMPLATE',''),
('STOCK_STATUS','',false,true,'','','STOCK_STATUS_SMS_MESSAGE_TEMPLATE'),
('RATIONING','',true,true,'','RATIONING_EMAIL_MESSAGE_TEMPLATE','RATIONING_SMS_MESSAGE_TEMPLATE'),
('FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT','STOCKOUT',true,true,'alert_facility_stockedout','SMS_FACILITY_STOCKED_OUT','EMAIL_FACILITY_STOCKED_OUT'),
('REQUISITION_APPROVED','ALERT',true,true,'alert_requisition_approved','SMS_REQUISITION_APPROVED','EMAIL_REQUISITION_APPROVED');

