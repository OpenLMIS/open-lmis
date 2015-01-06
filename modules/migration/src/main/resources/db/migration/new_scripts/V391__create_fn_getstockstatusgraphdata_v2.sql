--   Function: fn_getstockstatusgraphdata(integer, integer, integer, character varying)

DROP FUNCTION IF EXISTS fn_getstockstatusgraphdata(integer, integer, integer, character varying);

CREATE OR REPLACE FUNCTION fn_getstockstatusgraphdata(IN in_programid integer, IN in_geographiczoneid integer, IN in_periodid integer, IN in_productid character varying)
  RETURNS TABLE(productid integer, productname text, periodid integer, periodname text, periodyear integer, quantityonhand integer, quantityconsumed integer, amc integer) AS
$BODY$
DECLARE
stockStatusQuery VARCHAR;
finalQuery            VARCHAR;
rowSS                 RECORD;
v_scheduleid integer;
v_check_id integer;
rec RECORD;
BEGIN
SELECT scheduleid INTO v_scheduleid FROM processing_periods WHERE id = in_periodid;
v_scheduleid = COALESCE(v_scheduleid,0);
EXECUTE 'CREATE TEMP TABLE _stock_status (
productid integer,
productname text,
periodid integer,
periodname text,
periodyear integer,
quantityonhand integer,
quantityconsumed integer,
amc integer
) ON COMMIT DROP';
stockStatusQuery :=
'SELECT
product_id productid,
product_primaryname productname,
processing_periods_id periodid,
processing_periods_name periodname,
EXTRACT (
''year''
FROM
processing_periods_start_date
) periodyear,
SUM (stockinhand) quantityonhand,
SUM (quantitydispensed) quantityconsumed,
SUM (amc) amc
FROM
vw_requisition_detail_2
WHERE
program_id = '|| in_programid ||'
AND (zone_id = '|| in_geographiczoneid || ' OR ' || in_geographiczoneid ||' = 0)
AND product_id IN ('|| in_productid ||')
AND processing_periods_id IN (select id from processing_periods where scheduleid = '|| v_scheduleid || ' AND id <= '|| in_periodid || ' order by id desc limit 4)
GROUP BY
product_id,
product_primaryname,
processing_periods_id,
processing_periods_name,
EXTRACT (
''year''
FROM
processing_periods_start_date
)';
FOR rowSS IN EXECUTE stockStatusQuery
LOOP
EXECUTE
'INSERT INTO _stock_status VALUES (' ||
COALESCE(rowSS.productid,0) || ',' ||
quote_literal(rowSS.productname::text) || ',' ||
COALESCE(rowSS.periodid,0) || ',' ||
quote_literal(rowSS.periodname::text) || ',' ||
COALESCE(rowSS.periodyear,0) || ',' ||
COALESCE(rowSS.quantityonhand,0) || ',' ||
COALESCE(rowSS.quantityconsumed,0) || ',' ||
COALESCE(rowSS.amc,0) || ')';
END LOOP;
FOR rec IN
select distinct ss.productid, ss.productname, s.id periodid, s.name periodname, EXTRACT ('year' FROM startdate) periodyear from _stock_status ss
cross join (select * from processing_periods
where scheduleid = v_scheduleid and id <= in_periodid order by id desc
limit 4) s order by ss.productid, s.id desc LOOP
select t.productid into v_check_id from _stock_status t where t.productid = rec.productid and t.periodid = rec.periodid;
v_check_id = COALESCE(v_check_id,0);
if v_check_id = 0 THEN
insert into _stock_status values (rec.productid, rec.productname, rec.periodid, rec.periodname, rec.periodyear, 0, 0, 0);
end if;
END LOOP;
finalQuery := 'SELECT productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc FROM  _stock_status order by periodid';
RETURN QUERY EXECUTE finalQuery;
END;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_getstockstatusgraphdata(integer, integer, integer, character varying)
  OWNER TO postgres;
