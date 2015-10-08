-- Function: fn_vaccine_facility_nth_rnr(integer, integer, integer, integer, integer)

DROP FUNCTION IF EXISTS fn_vaccine_facility_nth_rnr(integer, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_vaccine_facility_nth_rnr(IN in_program_id integer, IN in_period_id integer, IN in_facility_id integer, IN in_productid integer, IN in_nth integer DEFAULT 0)
  RETURNS TABLE(reportid integer, productcode character varying, openingbalance integer, quantityreceived integer, quantityissued integer, quantityvvmalerted integer, quantityfreezed integer, quantityexpired integer, quantitydiscardedunopened integer, quantitydiscardedopened integer, quantitywastedother integer, endingbalance integer, closingbalance integer, daysstockedout integer, price numeric, periodid integer) AS
$BODY$
/*
 This function function build anlystical table of key logistics indicators of product for past 4 periods of single reporting facility

 indicators include:
 opening balanace
 quantity received,
 quantity dispensed
 adjusted consumption
 adjustment
 stockinhand
 quantity requested
 quantity approved
 quanity expired
 price
 
*/

DECLARE

-- return values
v_rnr_id integer;
finalQuery            VARCHAR;

-- temp
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;

BEGIN

t_product_id = in_productid;

-- get price
--select id into t_product_id from products where code = in_productcode; 
select currentprice into t_price from program_products where productid = t_product_id and programid = in_program_id;

-- get start date
t_start_date = (select startdate::date from processing_periods where id = in_period_id);
t_period_id = COALESCE(in_period_id,0);

i := 0;

-- not executed if in_nth is 0(current period)
FOR i in 1..in_nth 
 LOOP
 i = i+1;
 select vaccine_reports.periodid, processing_periods.startdate::date into t_id, t_date 
 from vaccine_reports
 join processing_periods ON vaccine_reports.periodid = processing_periods.id
 where processing_periods.startdate < t_start_date 
   and facilityid = in_facility_id 
   and vaccine_reports.programid = in_program_id
  order by processing_periods.startdate desc
 limit 1;
 t_start_date = t_date;
 t_period_id = COALESCE(t_id,0);

 EXIT WHEN t_period_id =  0;
 
 END LOOP;

if t_period_id > 0 then

-- get requisition id of nth period
select id into v_rnr_id 
 from vaccine_reports
 where vaccine_reports.periodid = t_period_id 
 and facilityid = in_facility_id 
 and vaccine_reports.programid = in_program_id 
 order by vaccine_reports.periodid desc
 limit 1;

 v_rnr_id = COALESCE(v_rnr_id,0);

 finalQuery :=
'select
reportid,
productcode,
openingbalance,
quantityreceived,
quantityissued,
quantityvvmalerted,
quantityfreezed,
quantityexpired,
quantitydiscardedunopened,
quantitydiscardedopened,
quantitywastedother,
endingbalance,
closingbalance,
daysstockedout,'
||t_price|| ' price, '|| t_period_id || ' periodid '||'
from vaccine_report_logistics_line_items where reportid = '||v_rnr_id || ' and productid = '||in_productid;

ELSE

finalQuery :=
       'select
				null::int reportid,
				null::character varying productcode,
				null::int openingbalance,
				null::int quantityreceived,
				null::int quantityissued,
				null::int quantityvvmalerted,
				null::int quantityfreezed,
				null::int quantityexpired,
				null::int quantitydiscardedunopened,
				null::int quantitydiscardedopened,
				null::int quantitywastedother,
				null::int endingbalance,
				null::int closingbalance,
				null::int daysstockedout,
				null::numeric price,
				null::integer periodid';
end if;

RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_vaccine_facility_nth_rnr(integer, integer, integer, integer, integer)
  OWNER TO postgres;
