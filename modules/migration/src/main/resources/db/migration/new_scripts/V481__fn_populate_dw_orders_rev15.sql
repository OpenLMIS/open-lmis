-- Function: fn_populate_dw_orders(integer)

DROP FUNCTION if exists fn_populate_dw_orders(integer);

CREATE OR REPLACE FUNCTION fn_populate_dw_orders(in_flag integer)
  RETURNS character varying AS
$BODY$
DECLARE
rec RECORD ;
rec2 RECORD ;
li INTEGER ;
msg CHARACTER VARYING (2000) ;
v_programid INTEGER ;
v_programcode CHARACTER VARYING (100);
v_geographiczoneid INTEGER ;
v_facilityid INTEGER ;
v_facilitycode CHARACTER VARYING (50) ;
v_periodid INTEGER ;
v_rnrid INTEGER ;
v_status CHARACTER VARYING (20) ;
v_productid INTEGER ;
v_productcode CHARACTER VARYING (50) ;
v_quantityrequested INTEGER ;
v_quantityapproved INTEGER ;
v_quantityapprovedprev INTEGER ;
v_quantityreceived INTEGER ;
v_createddate TIMESTAMP ;
v_approveddate TIMESTAMP ;
v_shippeddate TIMESTAMP ;
v_receiveddate TIMESTAMP ;
v_stocking CHARACTER (1) ;
v_reporting CHARACTER (1) ;
v_programname CHARACTER VARYING (50) ;
v_facilityname CHARACTER VARYING (50) ;
v_productprimaryname CHARACTER VARYING (150) ;
v_productfullname CHARACTER VARYING (250) ;
v_geographiczonename CHARACTER VARYING (250) ;
v_processingperiodname CHARACTER VARYING (250) ;
v_soh INTEGER;
v_amc  INTEGER;
v_mos numeric(6,1);
v_previousstockinhand integer;
v_emergency boolean;
v_supervisorynodeid INTEGER;
v_requisitiongroupid integer;
v_requisitiongroupname character varying(50);
v_facilitytypeid integer;
v_facilitytypename character varying(50);
v_scheduleid integer;
v_schedulename character varying(50);
v_productcategoryid integer;
v_productcategoryname character varying(150);
v_productgroupid integer;
v_productgroupname character varying(250);
v_stockedoutinpast boolean;
v_suppliedinpast boolean;
v_mossuppliedinpast numeric(6,1);
v_late_days integer;
previous_periodid integer;
v_totalsuppliedinpast integer;
v_mossuppliedinpast_denominator integer;
v_previousrnrid integer;
v_lastupdatedate TIMESTAMP ;
v_tracer boolean;
v_skipped boolean;
v_stockoutdays integer;
v_last_run TIMESTAMP;
v_this_run TIMESTAMP; -- timestamp of this function run
v_rnr_modifieddate TIMESTAMP;
v_updated boolean;
v_periodstartdate date;
v_periodenddate date;

---
v_openingbalance integer;
v_dispensed integer;
v_adjustment integer;
v_quantityordered integer;
v_quantityshipped INTEGER ;
v_dateordered date;
v_dateshipped date;


p1 RECORD;
p2 RECORD;


BEGIN
msg := 'Data saved successfully' ;
v_this_run = now();
v_updated = 'f'; -- 

if in_flag = 1 then
-- last dw_orders run date
 select a.last_run into v_last_run from (select modifieddate last_run
      from dw_orders order by 1 desc limit 1) a;

  if length(COALESCE(v_last_run::text,''))= 0 then 
   v_last_run = (now() - interval '12 month'::interval);
  end if;

else 
 -- beginning of last 12 month period
 v_last_run = (now() - interval '12 month'::interval);
end if;

msg ='before delete';
-- delete rnr older than 12 months
DELETE FROM dw_orders where modifieddate::date < (now()::date - interval '12 month'::interval)::date;

-- delete rnr modified/add in last 12 months
if in_flag = 1 then
DELETE FROM dw_orders where 
rnrid in (
SELECT
vw_requisition_detail_dw.requisition_id
FROM
vw_requisition_detail_dw
where skipped = 'f'
and product_tracer = 't'
and modifieddate > v_last_run 
and requisition_status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')
and (COALESCE(stockinhand,0)
+ COALESCE(beginningbalance,0)
+ COALESCE(quantitydispensed,0)
+ COALESCE(quantityreceived,0)
+ COALESCE(stockoutdays,0)
+ abs(COALESCE(totallossesandadjustments,0))) >  0 
);

ELSE
-- delete every thing
 delete from dw_orders;
end if;

msg ='before requisition detail data';
-- fetch requisition added/modified data after last run
FOR rec IN

SELECT
*
FROM
vw_requisition_detail_dw 
where skipped = 'f'
and product_tracer = 't'
and modifieddate > v_last_run 
and requisition_status not in ('INITIATED', 'SUBMITTED', 'SKIPPED')
and (COALESCE(stockinhand,0)
+ COALESCE(beginningbalance,0)
+ COALESCE(quantitydispensed,0)
+ COALESCE(quantityreceived,0)
+ COALESCE(stockoutdays,0)
+ abs(COALESCE(totallossesandadjustments,0))) >  0


LOOP --fetch the table row inside the loop

v_programid = rec.program_id ;
v_programcode = rec.program_code;
v_geographiczoneid = rec.geographic_zone_id;
v_facilityid = rec.facility_id ;
v_facilitycode = rec.facility_code ;
v_periodid = rec.period_id ;
v_rnrid = rec.requisition_id ;
v_status = rec.requisition_status ;
v_productid = rec.product_id ;
v_productcode = rec.product_code ;
v_quantityrequested = rec.quantityrequested ;
v_quantityapproved = rec.quantityapproved ;
v_quantityreceived = rec.quantityreceived ; -- will set the date later
v_createddate = NULL ;
v_approveddate = NULL ;
v_shippeddate = NULL ;
v_receiveddate = NULL ;
v_programname = rec.program_name ;
v_facilityname = rec.facility_name ;
v_productprimaryname = rec.product_primaryname ;
v_productfullname = rec.product_fullname;
v_processingperiodname = rec.period_name ;
v_soh = rec.stockinhand;
v_amc = rec.amc;
v_mos = CASE WHEN v_amc > 0 THEN v_soh / v_amc ELSE 0 END;
v_emergency = rec.requisition_emergency;
v_supervisorynodeid = NULL;
v_requisitiongroupid = NULL;
v_requisitiongroupname = NULL;
v_facilitytypeid = rec.facility_type_id;
v_facilitytypename = rec.facility_type_name;
v_scheduleid = rec.processing_schedule_id;
v_schedulename = rec.processing_schedule_name;
v_productcategoryid = rec.product_category_id;
v_productcategoryname = rec.product_category_name;
v_productgroupid = rec.product_group_id;
v_productgroupname = rec.product_group_id;
v_stockedoutinpast = 'N';
v_suppliedinpast = 'N';
v_mossuppliedinpast = 1;
v_geographiczonename =  rec.geographic_zone_name;
v_previousstockinhand =  rec.previousstockinhand;
v_tracer =  rec.product_tracer;
v_skipped =  rec.skipped;
v_stockoutdays =  rec.stockoutdays;
v_totalsuppliedinpast = 0;
v_rnr_modifieddate = rec.modifieddate;
v_periodstartdate = rec.period_start_date::date;
v_periodenddate = rec.period_end_date::date;

v_openingbalance = rec.beginningbalance;
v_dispensed = rec.quantitydispensed;
v_adjustment = rec.totallossesandadjustments;
v_quantityordered = rec.quantityordered;
v_quantityshipped = rec.quantityshipped;
v_dateordered = rec.ordereddate::date;
v_dateshipped = rec.shippeddate::date;


if v_previousstockinhand = 0 then
v_stockedoutinpast = 'Y';
end if;

msg ='before last two';
-- calculate last two consecutive period stock and supply
select * from fn_previous_rnr_detail(v_programid, v_periodid,v_facilityid,v_productcode) into p1;
v_previousrnrid = COALESCE(p1.rnrid,0);

select periodid into previous_periodid from requisitions where requisitions.id = p1.rnrid;
previous_periodid = COALESCE(previous_periodid,0);

select * from fn_previous_rnr_detail(v_programid, previous_periodid,v_facilityid,v_productcode) into p2;

v_mossuppliedinpast_denominator = 0;

if COALESCE(v_soh,0) > 0 then
v_mossuppliedinpast_denominator  = v_amc;
elsif COALESCE(p1.stockinhand,0) > 0 then
v_mossuppliedinpast_denominator  = p1.amc;
elsif COALESCE(p2.stockinhand,0) > 0 then
v_mossuppliedinpast_denominator  = p2.amc;
end if;

if p1.stockinhand = 0 and p2.stockinhand = 0 then
v_stockedoutinpast = 'Y';
end if;

if p1.quantityreceived > 0 or p2.quantityreceived > 0 then
v_suppliedinpast = 'Y';
v_totalsuppliedinpast = COALESCE(p1.quantityreceived,0) + COALESCE(p2.quantityreceived,0);
end if;

v_mossuppliedinpast = 0;
if v_mossuppliedinpast_denominator > 0 then
v_mossuppliedinpast = v_totalsuppliedinpast /  v_mossuppliedinpast_denominator;
end if;
v_quantityapprovedprev = COALESCE(p1.quantityapproved,0);

-- calculate stocking efficiency
IF rec.stockinhand = 0 THEN
v_stocking = 'S' ;
ELSEIF rec.stockinhand > 0
AND rec.stockinhand <= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_minmonthsofstock,rec.facility_type_nominaleop),0)
) THEN
v_stocking = 'U' ;
ELSEIF rec.stockinhand > 0
AND rec.stockinhand >= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_maxmonthsofstock,rec.facility_type_nominalmaxmonth),0)
) THEN
v_stocking = 'O' ;
ELSEIF rec.stockinhand <= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_maxmonthsofstock,rec.facility_type_nominalmaxmonth),0))
AND rec.stockinhand >= (
COALESCE (rec.amc, 0) * COALESCE(COALESCE(rec.facility_approved_product_minmonthsofstock,rec.facility_type_nominaleop),0)) THEN
v_stocking = 'A' ;
ELSE
v_stocking = 'U' ;
END IF ;

msg ='before insert';
INSERT INTO dw_orders (
programid,
programcode,
geographiczoneid,
facilityid,
facilitycode,
periodid,
rnrid,
emergency,
status,
productid,
productcode,
quantityrequested,
quantityapproved,
quantityreceived,
quantityapprovedprev,
createddate,
approveddate,
shippeddate,
receiveddate,
stocking,
reporting,
programname,
facilityname,
productprimaryname,
productfullname,
geographiczonename,
processingperiodname,
soh,
amc,
mos,
requisitiongroupid,
requisitiongroupname,
facilitytypeid,
facilitytypename,
scheduleid,
schedulename,
productcategoryid,
productcategoryname,
productgroupid,
productgroupname,
stockedoutinpast,
suppliedinpast,
mossuppliedinpast,
supervisorynodeid,
modifieddate,
tracer,
skipped,
stockoutdays,
rnrmodifieddate,
periodstartdate,
periodenddate,
openingBalance,
dispensed,
adjustment,
quantityordered,
quantityshipped,
dateordered,
dateshipped
)
VALUES
(
v_programid,
v_programcode,
v_geographiczoneid,
v_facilityid,
v_facilitycode,
v_periodid,
v_rnrid,
v_emergency,
v_status,
v_productid,
v_productcode,
v_quantityrequested,
v_quantityapproved,
v_quantityreceived,
v_quantityapprovedprev,
v_createddate,
v_approveddate,
v_shippeddate,
v_receiveddate,
v_stocking,
v_reporting,
v_programname,
v_facilityname,
v_productprimaryname,
v_productfullname,
v_geographiczonename,
v_processingperiodname,
v_soh,
v_amc,
v_mos,
v_requisitiongroupid,
v_requisitiongroupname,
v_facilitytypeid,
v_facilitytypename,
v_scheduleid,
v_schedulename,
v_productcategoryid,
v_productcategoryname,
v_productgroupid,
v_productgroupname,
v_stockedoutinpast,
v_suppliedinpast,
v_mossuppliedinpast,
v_supervisorynodeid,
v_this_run,
v_tracer,
v_skipped,
v_stockoutdays,
v_rnr_modifieddate,
v_periodstartdate,
v_periodenddate,
v_openingBalance,
v_dispensed,
v_adjustment,
v_quantityordered,
v_quantityshipped,
v_dateordered,
v_dateshipped
) ;

v_updated = 't';
END loop ; -- update rnr create date

msg ='x11';

IF v_updated = 't' THEN
UPDATE dw_orders o
SET createddate = r.createddate
FROM
requisitions r
WHERE
o.rnrid = r. ID
and o.modifieddate >= v_this_run; 


UPDATE dw_orders o
SET initiateddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'INITIATED'
and o.modifieddate >= v_this_run;

UPDATE dw_orders o
SET submitteddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'SUBMITTED'
and o.modifieddate >= v_this_run;


UPDATE dw_orders o
SET authorizeddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'AUTHORIZED'
and o.modifieddate >= v_this_run;

UPDATE dw_orders o
SET inapprovaldate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'IN_APPROVAL'
and o.modifieddate >= v_this_run;


UPDATE dw_orders o
SET approveddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'APPROVED'
and o.modifieddate >= v_this_run;

UPDATE dw_orders o
SET releaseddate = r.createddate
FROM
requisition_status_changes r
WHERE
o.rnrid = r.rnrid
AND r.status = 'RELEASED'
and o.modifieddate >= v_this_run;

SELECT value from configuration_settings where key='LATE_REPORTING_DAYS' INTO v_late_days;
v_late_days = COALESCE(v_late_days,10);
UPDATE dw_orders o
SET reporting = CASE
WHEN (EXTRACT (DAY FROM r.createddate) - (select EXTRACT (DAY FROM startdate) from processing_periods where id = r.periodid)) > v_late_days THEN
'L'
ELSE
'O'
END
FROM
requisitions r
WHERE
o.rnrid = r. ID  -- update rnr approved date
and o.modifieddate >= v_this_run;

UPDATE dw_orders o
SET shippeddate = s.shippeddate,
quantityshipped = s.quantityshipped
FROM
shipment_line_items s
WHERE
o.rnrid = s.orderid
AND o.productcode = s.productcode -- update rnr received date from pod
and o.modifieddate >= v_this_run;

UPDATE dw_orders o
SET receiveddate = P .receiveddate
FROM
pod P
WHERE
o.rnrid = P .orderid 
and o.modifieddate >= v_this_run;


UPDATE dw_orders o
SET rmnch = 't'
 where o.productid in (
  select products.id from program_products 
   join products on program_products.productid = products.id 
   join programs on program_products.programid = programs.id
   where lower(programs.code) = 'rmnch'
)
and o.modifieddate >= v_this_run;



msg = fn_populate_alert_facility_stockedout(1);
msg = fn_populate_alert_requisition_approved(1);
msg = fn_populate_alert_requisition_pending(1);
msg = fn_populate_alert_requisition_rejected(1);
msg = fn_populate_alert_requisition_emergency(1);
END IF;
msg := 'Data saved successfully' ;
RETURN msg; 

EXCEPTION
WHEN OTHERS THEN
RETURN msg || ' Error populating data. Please consult database administrtor. ' || SQLERRM;
END ; $BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_populate_dw_orders(integer)
  OWNER TO openlmis;


