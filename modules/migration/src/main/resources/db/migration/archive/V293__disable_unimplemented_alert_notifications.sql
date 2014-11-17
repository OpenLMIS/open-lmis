--Disable alerts which are not yet implemented by setting both email and sms to false
update alerts
set email = false, sms = false
where alerttype in ('COMMODITY_RATIONED','EMERGENCY_REQUISITION','FACILITY_WITH_EMPTY_POD','UNSCHEDULED_ORDER','LATE_RESUPPLIED_FACILITY',
'FACILITY_STOCKED_OUT_OF_TRACER_PRODUCT','REQUISITION_APPROVED')