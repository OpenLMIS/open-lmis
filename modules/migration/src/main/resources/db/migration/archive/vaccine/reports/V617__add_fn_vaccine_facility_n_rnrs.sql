-- Function: fn_vaccine_facility_n_rnrs(character varying, character varying, character varying, integer)

 DROP FUNCTION IF EXISTS fn_vaccine_facility_n_rnrs(character varying, character varying, character varying, integer);

CREATE OR REPLACE FUNCTION fn_vaccine_facility_n_rnrs(IN in_program_code character varying, IN in_facility_code character varying, IN in_product_code character varying, IN in_n integer DEFAULT 4)
  RETURNS TABLE(program_code text, facility_code text, product_code text, period_id integer, opening_balance integer, quantity_received integer, quantity_issued integer, quantity_vvm_alerted integer, quantity_freezed integer, quantity_expired integer, quantity_discarded_unopened integer, quantity_discarded_opened integer, quantity_wasted_other integer, ending_balance integer, closing_balance integer, days_stockedout integer, price numeric) AS
$BODY$
/*
 This function function build anlystical table of key logistics indicators of product 
  for past n periods of single reporting facility

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
t_program_id integer;
t_facility_id integer;

BEGIN

-- get ids, the nth function called below requires id except for product 
select id into t_product_id from products where lower(code) = lower(in_product_code); 
select id into t_program_id from programs where lower(code)= lower(in_program_code);
select id into t_facility_id from facilities where lower(code)= lower(in_facility_code);
select currentprice into t_price from program_products where productid = t_product_id and programid = t_program_id;

select vaccine_reports.periodid, processing_periods.startdate::date into t_id, t_date 
 from vaccine_reports
 join processing_periods ON vaccine_reports.periodid = processing_periods.id
 where facilityid = t_facility_id 
   and vaccine_reports.programid = t_program_id
  order by processing_periods.startdate desc
 limit 1;

-- get start date
t_start_date = t_date;
t_period_id = COALESCE(t_id,0);

i := 0;
finalQuery = '';
-- not executed if in_nth is 0(current period)
FOR i in 0..in_n-1
 LOOP
 if i = 0 THEN
  finalQuery := '';
 ELSE
  finalQuery := finalQuery || ' union all ';
 END IF;
 finalQuery := finalQuery ||
   'select '''||
    in_program_code|| '''::text program_code, '''||
    in_facility_code|| '''::text facility_code, '''||
    in_product_code|| '''::text product_code, '||
    'periodid period_id,
		openingbalance opening_balance,
		quantityreceived quantity_received,
		quantityissued quantity_issued,
		quantityvvmalerted quantity_vvm_alerted,
		quantityfreezed quantity_freezed,
		quantityexpired quantity_expired,
		quantitydiscardedunopened quantity_discarded_unopened,
		quantitydiscardedopened quantity_discarded_opened,
		quantitywastedother quantity_wasted_other,
		endingbalance ending_balance,
		closingbalance closing_balance,
		daysstockedout days_stockedout,
		price
    from fn_vaccine_facility_nth_rnr('||t_program_id||','||t_period_id||','||t_facility_id||','||t_product_id||','||i||')';
 i = i+1;
end loop;

RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_vaccine_facility_n_rnrs(character varying, character varying, character varying, integer)
  OWNER TO postgres;
