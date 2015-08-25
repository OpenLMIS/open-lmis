
-- Function: fn_previous_cb(integer, integer, integer, character varying)

DROP FUNCTION IF EXISTS fn_previous_rnr_detail(integer, integer, integer, character varying);

CREATE OR REPLACE FUNCTION fn_previous_rnr_detail(v_program_id integer, v_period_id integer, v_facility_id integer, v_productcode character varying)
  RETURNS TABLE(rnrid integer, productcode character varying, beginningbalance integer, quantityreceived integer, 
    quantitydispensed integer, stockinhand integer, quantityrequested integer,  calculatedorderquantity integer, quantityapproved integer,
    totallossesandadjustments integer,reportingdays integer, previousstockinhand integer, periodnormalizedconsumption integer, amc integer) AS
$BODY$
DECLARE
v_ret integer;
v_prev_id integer;
v_rnr_id integer;
finalQuery            VARCHAR;
BEGIN

select id into v_rnr_id from requisitions where requisitions.periodid < v_period_id and facilityid = v_facility_id and requisitions.programid = v_program_id order by requisitions.periodid desc limit 1;
v_rnr_id = COALESCE(v_rnr_id,0);

finalQuery :=
 'select 
rnrid,
productcode,
beginningbalance,
quantityreceived,
quantitydispensed,
stockinhand,
quantityrequested,
calculatedorderquantity,
quantityapproved,
totallossesandadjustments,
reportingdays,
previousstockinhand,
periodnormalizedconsumption,
amc
from requisition_line_items where rnrid = '||v_rnr_id || ' and productcode = '||chr(39)||v_productcode||chr(39);

  RETURN QUERY EXECUTE finalQuery;


END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100;
ALTER FUNCTION fn_previous_rnr_detail(integer, integer, integer, character varying)
  OWNER TO postgres;
