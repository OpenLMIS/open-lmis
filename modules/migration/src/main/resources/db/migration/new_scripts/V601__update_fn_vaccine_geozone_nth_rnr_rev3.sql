-- Function: fn_vaccine_geozone_nth_rnr(integer, integer, integer, integer, integer)

DROP FUNCTION IF EXISTS fn_vaccine_geozone_nth_rnr(integer, integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION fn_vaccine_geozone_nth_rnr(IN in_program_id integer, IN in_period_id integer, IN in_geographiczone_id integer, IN in_product_id integer, IN in_nth integer DEFAULT 0)
  RETURNS TABLE(periodid integer, geographiczoneid integer, openingbalance integer, quantityreceived integer, quantityissued integer, quantityvvmalerted integer, quantityfreezed integer, quantityexpired integer, quantitydiscardedunopened integer, quantitydiscardedopened integer, quantitywastedother integer, endingbalance integer, closingbalance integer, daysstockedout integer, price numeric) AS
$BODY$
DECLARE
v_rnr_id integer;
finalQuery            VARCHAR;
i integer;
t_period_id integer;
t_start_date date;
t_id integer; -- temp
t_date date; -- temp2
t_price numeric(20,2);
t_product_id integer;
t_quantity_expired integer = 0;
t_li_id integer;
t_where_1 varchar;
t_group_by varchar;
t_schedule_id integer;
BEGIN
t_product_id = in_product_id;
t_period_id = COALESCE(in_period_id,0);
select currentprice into t_price from program_products where productid = t_product_id and programid = in_program_id;
select startdate::date, scheduleid into t_start_date, t_schedule_id from processing_periods where id = t_period_id;
i := 0;
FOR i in 1..in_nth
LOOP
i = i+1;
SELECT
vaccine_reports.periodid,
processing_periods.startdate::date into t_id, t_date
FROM
vaccine_reports
JOIN processing_periods ON vaccine_reports.periodid = processing_periods.id
INNER JOIN facilities ON facilities.id = vaccine_reports.facilityid
INNER JOIN vw_districts ON facilities.geographiczoneid = vw_districts.district_id
where processing_periods.scheduleid = t_schedule_id
and processing_periods.startdate::date < t_start_date
order by processing_periods.startdate desc
limit 1;
t_start_date = t_date;
t_period_id = COALESCE(t_id,0);
EXIT WHEN t_period_id =  0;
END LOOP;
t_where_1  = ' where productid = '||in_product_id||' and (district_id = '||in_geographiczone_id||' or region_id = '||in_geographiczone_id||' or zone_id = '||in_geographiczone_id || ' or parent =  '||in_geographiczone_id ||')';
t_group_by = ' group by vaccine_reports.perioid ';
if t_period_id > 0 then
t_where_1 = t_where_1 || ' and periodid = '||t_period_id;
finalQuery :=
'SELECT '||
t_period_id || '::integer period_d, '||
in_geographiczone_id || '::integer geographiczoneid, '||'
sum(openingbalance)::int openingbalance,
sum(quantityreceived)::int quantityreceived,
sum(quantityissued)::int quantityissued,
sum(quantityvvmalerted)::int quantityvvmalerted,
sum(quantityfreezed)::int quantityfreezed,
sum(quantityexpired)::int quantityexpired,
sum(quantitydiscardedunopened)::int quantitydiscardedunopened,
sum(quantitydiscardedopened)::int quantitydiscardedopened,
sum(quantitywastedother)::int quantitywastedother,
sum(endingbalance)::int endingbalance,
sum(closingbalance)::int closingbalance,
sum(daysstockedout)::int daysstockedout, 0::numeric price
FROM
vaccine_report_logistics_line_items
JOIN vaccine_reports ON vaccine_report_logistics_line_items.reportid = vaccine_reports.id
INNER JOIN processing_periods ON processing_periods.id = vaccine_reports.periodid
INNER JOIN facilities ON facilities.id = vaccine_reports.facilityid
INNER JOIN vw_districts ON vw_districts.district_id = facilities.geographiczoneid '||
t_where_1;
ELSE
finalQuery :=
'select
null::int periodid,
null::int geographiczoneid,
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
null::numeric price';
end if;
RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_vaccine_geozone_nth_rnr(integer, integer, integer, integer, integer)
  OWNER TO postgres;
