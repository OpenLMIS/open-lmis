-- Function: fn_get_rmnch_stock_status_data(integer, integer, character varying)
-- remove programid as the program is fixed

DROP FUNCTION IF EXISTS fn_get_rmnch_stock_status_data(integer, integer, integer, character varying);
DROP FUNCTION IF EXISTS fn_get_rmnch_stock_status_data(integer, integer, character varying);

CREATE OR REPLACE FUNCTION fn_get_rmnch_stock_status_data(IN in_geographiczoneid integer, IN in_periodid integer, IN in_productid character varying)
  RETURNS TABLE(productid integer, productname text, periodid integer, periodname text, periodyear integer, quantityonhand integer, quantityconsumed integer, amc integer) AS
$BODY$
DECLARE
stockStatusQuery VARCHAR ;
finalQuery VARCHAR ;
rowSS RECORD ;
v_scheduleid INTEGER ;
v_check_id INTEGER ;
rec RECORD ;
BEGIN
SELECT scheduleid INTO v_scheduleid FROM processing_periods
WHERE  ID = in_periodid ;
v_scheduleid = COALESCE (v_scheduleid, 0) ;

EXECUTE 'CREATE TEMP TABLE _stock_status (
productid integer,
productname text,
periodid integer,
periodname text,
periodyear integer,
quantityonhand integer,
quantityconsumed integer,
amc integer
) ON COMMIT DROP' ;

stockStatusQuery := 
'SELECT  
s.productid,
s.productname,
s.periodid,
s.periodname,
s.reportyear periodyear,
SUM (s.stockonhand) quantityonhand,
SUM (s.issues) quantityconsumed,
SUM (s.amc) amc
 from vw_e2e_stock_status s 
where rmnch =''t''
and (s.geographiczoneid = ' || in_geographiczoneid || ' OR ' || in_geographiczoneid || ' = 0)
AND periodid IN (select id from processing_periods where scheduleid = ' || v_scheduleid || ' AND id <= ' || in_periodid || ' order by id desc limit 4)
and productid in (' || in_productid || ')
group by 
s.productid,
s.productname,
s.periodid,
s.periodname,
s.reportyear';


FOR rowSS IN EXECUTE stockStatusQuery
LOOP EXECUTE
'INSERT INTO _stock_status VALUES (' || COALESCE (rowSS.productid, 0) || ',' ||
quote_literal(rowSS.productname :: TEXT) || ',' ||
COALESCE (rowSS.periodid, 0) || ',' ||
quote_literal(rowSS.periodname :: TEXT) || ',' ||
COALESCE (rowSS.periodyear, 0) || ',' ||
COALESCE (rowSS.quantityonhand, 0) || ',' ||
COALESCE (rowSS.quantityconsumed, 0) || ',' ||
COALESCE (rowSS.amc, 0) || ')' ;
END LOOP ;
FOR rec IN SELECT DISTINCT
ss.productid,
ss.productname,
s. ID periodid,
s. NAME periodname,
EXTRACT ('year' FROM startdate) periodyear
FROM
_stock_status ss
CROSS JOIN (
SELECT
*
FROM
processing_periods
WHERE
scheduleid = v_scheduleid
AND ID <= in_periodid
ORDER BY
ID DESC
LIMIT 4
) s
ORDER BY
ss.productid,
s. ID DESC
LOOP
SELECT
T .productid INTO v_check_id
FROM
_stock_status T
WHERE
T .productid = rec.productid
AND T .periodid = rec.periodid ; v_check_id = COALESCE (v_check_id, 0) ;
IF v_check_id = 0 THEN
INSERT INTO _stock_status
VALUES
(
rec.productid,
rec.productname,
rec.periodid,
rec.periodname,
rec.periodyear,
0,
0,
0
) ;
END IF ;
END LOOP ;
finalQuery := 'SELECT productid, productname, periodid, periodname, periodyear, quantityonhand, quantityconsumed, amc FROM  _stock_status order by periodid' ;
RETURN QUERY EXECUTE finalQuery ;
END ;
$BODY$
  LANGUAGE plpgsql VOLATILE
  COST 100
  ROWS 1000;
ALTER FUNCTION fn_get_rmnch_stock_status_data(integer, integer, character varying)
  OWNER TO postgres;
